package com.itdr.common;

public class Const {

    //失败，成功通用状态码
    public static final int SUCCESS=200;
    public static final int DEFEATED=100;

    //session中当前用户的key
    public static String LOGIN_USER="login_user";

    //枚举管理状态码和状态信息
    public enum UsersEnmu{

        NEED_LOGIN(2,"需要登陆"),
        NO_LOGIN(101,"用户未登录");

        private int code;
        private String desc;

        UsersEnmu(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }


}
