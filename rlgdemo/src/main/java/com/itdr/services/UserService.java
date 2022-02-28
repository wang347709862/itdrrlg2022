package com.itdr.services;

import com.itdr.common.ServerResponse;
import com.itdr.pojo.Users;

public interface UserService {
    ServerResponse<Users> login(String username, String password);

    ServerResponse<Users> register(Users u);

    ServerResponse<Users> checkUsernameOrEmail(String str, String type);

    ServerResponse getInformation(Users user);

    ServerResponse updateInformation(Users u);
}
