package com.example.demo.service;

import com.example.demo.Utils.JwtUtils;
import com.example.demo.bean.User;
import com.example.demo.bean.View.UserLoginView;
import com.example.demo.global.Constant;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    UserMapper userMapper;
    @Override
    public User login(User user) {
        return userMapper.login(user);
    }

    /**
     *
     * @ HKj
     * @return

    @Override
    public Map<String, Object> userLogin(User user) {
        User resUser = userMapper.userLogin(user);

        Map<String, Object> resMap = new HashMap();

        if (resUser == null){
            resMap.put("resCode", Constant.LOGIN_CODE_E01);
            return  resMap;
        }

        int userType = resUser.getUserType();

        switch (userType){
            case Constant.USER_AGENCY_TYPE:
                resMap.put("resCode", Constant.SUCCESS_CODE);
//                resMap.put("agency", agencyMapper.getAgencyById(resUser.getAgencyId()));
                break;
            case Constant.USER_COMPANY_TYPE:
                resMap.put("resCode", Constant.SUCCESS_CODE);
//                TeaCompanyLoginView<PostalAddress> userView = teaCompanyMapper.getTeaCompanyById(resUser.getTeaCompanyId());
//                List<PostalAddress> poAddressList = postalAddressMapper.
//                        getPostalAddressByCompany(userView.getTeaCompanyId());
//                userView.setPostalAddress(poAddressList);

//                resMap.put("teaCompany", userView);
                break;
            case Constant.USER_ADMIN_TYPE:
                resMap.put("resCode", Constant.SUCCESS_CODE);
//                resMap.put("admin", adminMapper.getAdminById(resUser.getAdminId()));
                break;
            default:
                resMap.put("resCode", Constant.LOGIN_CODE_E02);
                return resMap;
        }

        //把user属性拷贝到user视图类
        UserLoginView userView = new UserLoginView();
        Date regDate = resUser.getRegisterTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        BeanUtils.copyProperties(resUser, userView);
        userView.setRegisterTime(regDate == null?"":sdf.format(regDate));

        resMap.put("user", userView);

        //生成token
        String jwtToken = JwtUtils.generateToken(user.getUserPhone(), userType);

        resMap.put("token", jwtToken);

        return resMap;
    }
     */

    /**
     *

    @Override
    public int userRegister(User user){

        int flag = 0;       //0: 表示注册失败、1：用户已经存在、2：注册成功

        Date date = new Date();
        user.setRegisterTime(date);
        Agency agency = null;
        TeaCompany teaCompany = null;
        Admin admin = null;

//        PostalAddress postalAddress = null;     //茶企地址信息

        List<User> list = userMapper.selectByNameOrEmailOrPhone(user.getUserName(),user.getUserEmail(),user.getUserPhone());

        if (list.size()>0) {
            flag = 1;
            return flag;
        }

        switch (user.getUserType()){
            case Constant.USER_AGENCY_TYPE:
                agency = new Agency();
                agency.setAgencyName(user.getUserName());
                agency.setAgencyPhone(user.getUserPhone());
                agency.setAgencyEmail(user.getUserEmail());
                agency.setRegisterTime(user.getRegisterTime());
                agency.setAgencyAddress(user.getUserAddress());
                agency.setExamineStatus(0);


//                agencyMapper.insertAgency(agency);
                user.setAgencyId(agency.getAgencyId());
                user.setTeaCompanyId(-1);
                user.setAdminId(-1);

                break;
            case Constant.USER_COMPANY_TYPE:
                teaCompany = new TeaCompany();
//                postalAddress = new PostalAddress();
                teaCompany.setTeaCompanyName(user.getUserName());
                teaCompany.setTeaCompanyPhone(user.getUserPhone());
                teaCompany.setTeaCompanyEmail(user.getUserEmail());
                teaCompany.setRegisterTime(user.getRegisterTime());


                break;
            case Constant.USER_ADMIN_TYPE:
                admin = new Admin();
                admin.setAdminName(user.getUserName());
                admin.setAdminPhone(user.getUserPhone());
                admin.setAdminPassword(user.getUserPassword());
                admin.setRegisterTime(user.getRegisterTime());
                user.setAdminId(admin.getAdminId());
                user.setAgencyId(-1);
                user.setTeaCompanyId(-1);
                break;
        }
        System.out.println(user.toString());
        if (userMapper.userRegister(user)){
            flag = 2;
        }

        return flag;
    }
     */
}
