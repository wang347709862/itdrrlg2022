package com.itdr.controllers.portal;

import com.itdr.common.Const;
import com.itdr.common.ServerResponse;
import com.itdr.pojo.Users;
import com.itdr.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@ResponseBody
@RequestMapping("/user/")
public class UserController {

    @Autowired
    UserService userService;

    //用户登录
    @RequestMapping("login.do")
    public ServerResponse<Users> login(String username, String password, HttpSession session){
        ServerResponse<Users> sr=userService.login(username,password);
        //当成功时data才有用户数据
        if(sr.isSuccess()){
            Users u = sr.getData();
            //包含密码的完整用户放在session里，有密码才方便做其他操作如自动登录
            session.setAttribute(Const.LOGIN_USER,u);

            //重新封装不包含密码的用户返回响应
            Users u2=new Users();
            u2.setId(u.getId());
            u2.setUsername(u.getUsername());
            u2.setEmail(u.getEmail());
            u2.setPhone(u.getPhone());
            u2.setCreateTime(u.getCreateTime());
            u2.setUpdateTime(u.getUpdateTime());
            u2.setPassword("");
            sr.setData(u2);
        }

        return sr;
    }

    //用户注册
    @RequestMapping("register.do")
    public ServerResponse<Users> register(Users u){
        ServerResponse<Users> sr=userService.register(u);
        return sr;
    }

    //用户名或邮箱是否有效
    @RequestMapping("check_valid.do")
    public ServerResponse<Users> checkUsernameOrEmail(String str,String type){
        ServerResponse<Users> sr=userService.checkUsernameOrEmail(str,type);
        return sr;
    }

    //获取当前登录用户信息
    @RequestMapping("get_user_info.do")
    public ServerResponse<Users> getUserInfo(HttpSession session){
        Users user = (Users) session.getAttribute(Const.LOGIN_USER);
        if(user==null){
            return ServerResponse.defeatedRS(Const.UsersEnmu.NO_LOGIN.getCode(),Const.UsersEnmu.NO_LOGIN.getDesc());
        }else{
            return ServerResponse.successRS(user);
        }
    }

    //退出登录
    @RequestMapping("logout.do")
    public ServerResponse<Users> logout(HttpSession session){
       session.removeAttribute(Const.LOGIN_USER);
       return ServerResponse.successRS("退出成功");
    }

    //获取当前登录用户详细信息，因为session中可能不完整
    @RequestMapping("get_information.do")
    public ServerResponse<Users> getInformation(HttpSession session){
        Users user = (Users) session.getAttribute(Const.LOGIN_USER);
        if(user==null){
            return  ServerResponse.defeatedRS(Const.UsersEnmu.NO_LOGIN.getCode(),Const.UsersEnmu.NO_LOGIN.getDesc());
        }else{
            ServerResponse sr=userService.getInformation(user);
            return sr;
        }
    }

    //登录状态下更改部分个人信息（不包括用户名密码等）
    @RequestMapping("update_information.do")
    public ServerResponse<Users> updateInformation(Users u,HttpSession session){
        Users user = (Users) session.getAttribute(Const.LOGIN_USER);
        if(user==null){
            return  ServerResponse.defeatedRS(Const.UsersEnmu.NO_LOGIN.getCode(),Const.UsersEnmu.NO_LOGIN.getDesc());
        }else{
            //因为传入的邮箱地址等非重要信息，而更改需要id或username，所以要从session中拿到这部分数据赋值进去，再传给业务层
            u.setId(user.getId());
            u.setUsername(user.getUsername());
            ServerResponse sr=userService.updateInformation(u);
            //返回信息中取出更改后的用户存到session中
            session.setAttribute(Const.LOGIN_USER,sr.getData());
            return sr;
        }
    }

    //忘记密码，返回问题
    @RequestMapping("forget_question.do")
    public ServerResponse<Users> forgetQuestion(String username){
            return userService.forgetQuestion(username);
        }

    //根据用户名查询找回问题和答案，验证是否匹配
    @RequestMapping("forget_check_answer.do")
    public ServerResponse<Users> forgetCheckAnswer(String username,String question,String answer){
        return userService.forgetGetQuestion(username,question,answer);
    }

    //未登录状态，根据用户名修改密码，需要回答问题后的令牌
    @RequestMapping("forget_reset_password.do")
    public ServerResponse<Users> forgetResetPassword(String username,String passwordNew,String forgetToken){
        return userService.forgetResetPassword(username,passwordNew,forgetToken);
    }

    //登录状态，根据用户修改密码，需要验证旧密码
    @RequestMapping("reset_password.do")
    public ServerResponse<Users> resetPassword(String passwordOld,String passwordNew,HttpSession session){
        Users user = (Users) session.getAttribute(Const.LOGIN_USER);
        if (user == null) {
           return ServerResponse.defeatedRS(Const.UsersEnmu.NO_LOGIN.getCode(),Const.UsersEnmu.NO_LOGIN.getDesc());
        }else{
           return userService.resetPassword(user,passwordOld,passwordNew);
        }
    }
}
