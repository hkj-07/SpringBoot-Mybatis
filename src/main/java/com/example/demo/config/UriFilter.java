package com.example.demo.config;

import com.example.demo.Utils.JwtUtils;
import com.example.demo.bean.ResponseJson;
import com.example.demo.global.Constant;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author Hkj
 * @Date 2020/3/28
 */
@WebFilter(filterName = "UriFilter", urlPatterns = "/*")
public class UriFilter implements Filter {
    /**
    登录、注册不进行过滤
     **/
    private static final Set<String> ALLOWED_PATHS = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList("/user/login","/user/register", "/agency/getAgencyAllInfo", "/testItem/getTestItemInfo","/agency/getAgencyTestItemInfo")));

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("过滤器创建");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request =(HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        //不需要过滤的路径
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");
        boolean allowedPath = ALLOWED_PATHS.contains(path);

        if(allowedPath){
            filterChain.doFilter(request,response);
        } else{
            String token = request.getHeader("authorization"); //获取请求传来的token
            Claims claims = null;
            if (!"".equals(token)) {
                claims= JwtUtils.verifyJwt(token); //验证token
            }

            if (claims == null) {

                ResponseJson responseJson = new ResponseJson(Constant.AUTH_FAIL_CODE, "身份校验失败，请重新登录！");

                Gson gson = new Gson();
                String resJson =  gson.toJson(responseJson);

                response.setContentType("text/json;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(resJson);
            }else {
                filterChain.doFilter(request,response);
            }
        }
    }

    @Override
    public void destroy() {
        System.out.println("过滤器撤销");
    }

}
