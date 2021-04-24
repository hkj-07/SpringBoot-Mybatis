package com.example.demo.config;

import com.example.demo.Utils.IpAddressUtils;
import com.example.demo.Utils.JwtUtils;
import com.example.demo.bean.ResponseJson;
import com.example.demo.global.ApiStatisticsCount;
import com.example.demo.global.Constant;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
/**
 * @Author Jyo
 * @Date 2020/3/13
 */
@Slf4j
@WebFilter(filterName = "ApiFilter", urlPatterns = "/*")
public class ApiFilter implements Filter {
    @Autowired
    StringRedisTemplate stringRedisTemplate;


    //登录、注册不进行Token验证
    private static final Set<String> ALLOWED_PATHS = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList("/user/weChatAuth","/user/userRegisterAndLogin","/creatToken")));

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        //验证accessToken是否有效
        Map<String, Object> isValidMap = isTokenValid(request);
        boolean isValid = (Boolean) isValidMap.get("isValid");
        if (isValid){
            //身份校验成功，进行api日志统计
            statisticApi(servletRequest, servletResponse, filterChain);
        }else {

            //判断是否重新生成了token
            if (isValidMap.get("refreshMap") == null) {
                //accessToken和refreshToken都过期，身份校验失败，输出相关信息
                responseForClient(response, Constant.AUTH_FAIL_CODE_E1,  "身份验证失败，请重新登录！");
            }else {
                //accessToken和refreshToken重新生成，返回重新生成的token
                responseForClient(response, Constant.AUTH_FAIL_CODE_E2,  isValidMap.get("refreshMap"));
            }

        }

    }

    @Override
    public void destroy() {

    }

    /**
     * 统计API的调用次数，访问ip，调用所需时间
     */
    @SneakyThrows
    private void statisticApi(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        //API调用开始时间
        long start = System.currentTimeMillis();

        String uri = request.getRequestURI();
        if (!uri.isEmpty()){
            int subIndex = uri.indexOf("/", 2);
            String  subUri = uri.substring(subIndex + 1, uri.length()).replace("/", "_").toUpperCase();

            Field fields[] = ApiStatisticsCount.class.getFields();
            boolean isFilter = false; //请求接口是否在统计类中

            if(fields.length != 0){
                for (Field field:fields) {
                    if (subUri.equals(field.getName())){
                        int count = field.getInt(subUri);

                        field.set(subUri, ++count);

                        log.info("[Api Statistics] start. uri: {}, method: {}, ipAddress: {}", uri, request.getMethod(),
                                IpAddressUtils.getIPAddress(request));

                        //请求转发至目的地
                        filterChain.doFilter(servletRequest, servletResponse);
                        isFilter = true;

                        log.info("[Api Statistics] end. uri: {}, method: {}, ipAddress: {}, count: {}, duration_time: {}ms",
                                uri, request.getMethod(), IpAddressUtils.getIPAddress(request), count, System.currentTimeMillis()-start);
                    }
                }
                //请求接口不在统计类中，直接转发不做日志处理
                if (!isFilter){
                    filterChain.doFilter(servletRequest, servletResponse);
                }
            }else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }

    /**
     * 验证token是否失效
     */
    private Map<String, Object> isTokenValid( HttpServletRequest request){
        //不需要过滤的路径
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");
        boolean allowedPath = ALLOWED_PATHS.contains(path);
        //不过滤druid的内置页面
        boolean druidPath = path.contains("/druid");

        Map<String, Object> resMap = new HashMap<String, Object>();

        if(allowedPath || druidPath){
            //不做Token验证的直接放行
            resMap.put("isValid", true);
            return resMap;
        }else {
            String accessToken = request.getHeader("AccessAuthorization"); //获取请求传来的token
            Claims accessClaims = JwtUtils.verifyJwt(accessToken); //验证accessToken

            if (accessClaims == null) {
                resMap.put("isValid", false);
                return resMap;
            }

            if ((boolean)accessClaims.get("isExpired")) {
                String refreshToken = request.getHeader("RefreshAuthorization"); //获取请求传来的token
                Claims refreshClaims = JwtUtils.verifyJwt(refreshToken); //验证refreshToken

                //refreshToken有其他异常校验失败
                if (refreshClaims == null) {

                    resMap.put("isValid", false);
                    return resMap;
                }

                //refreshToken过期校验失败
                if ((boolean)refreshClaims.get("isExpired")) {

                    resMap.put("isValid", false);
                    return resMap;
                }

                //refreshToken有效,则重新生成accessToken和refreshToken,并将accessToken保存到redis
                Map<String, String> refreshMap = JwtUtils.creatTokenAndInsertRedis((Integer) refreshClaims.get("userId"));

                resMap.put("isValid", false);
                resMap.put("refreshMap", refreshMap);

                return resMap;
            }else {
                //比较redis和request中的token
                String uAccessToken = stringRedisTemplate.opsForValue().get(Constant.ACCESS_TOKEN_KEY+ accessClaims.get("userId"));
                if (uAccessToken == null || !uAccessToken.equals(accessToken)) {
                    resMap.put("isValid", false);
                    return resMap;
                }
                resMap.put("isValid", true);
                return resMap;
            }
        }
    }

    @SneakyThrows
    private void responseForClient(HttpServletResponse response, int statusCode, Object resObject){
        ResponseJson responseJson = new ResponseJson(statusCode, resObject);

        Gson gson = new Gson();
        String resJson =  gson.toJson(responseJson);

        response.setContentType("text/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(resJson);
    }

}
