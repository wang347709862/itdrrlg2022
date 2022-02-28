package com.itdr.services;

import com.itdr.common.ServerResponse;
import com.itdr.pojo.Users;

public interface UserService {
    ServerResponse<Users> login(String username, String password);

    ServerResponse<Users> register(Users u);

    ServerResponse<Users> checkUsernameOrEmail(String str, String type);

    ServerResponse getInformation(Users user);

    ServerResponse updateInformation(Users u);

    ServerResponse forgetQuestion(String username);

    ServerResponse<Users> forgetGetQuestion(String username, String question, String answer);

    ServerResponse<Users> forgetResetPassword(String username, String passwordNew,String forgetToken);

    ServerResponse<Users> resetPassword(Users user, String passwordOld, String passwordNew);
}
