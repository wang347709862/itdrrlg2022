package com.itdr.common;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {
    private  Integer status;
    private  T data;
    private  String msg;


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    //如果不成功，data为null，防止空指针
    //方法上注解，不返回该属性
    @JsonIgnore
    public boolean isSuccess(){
        return this.status==Const.SUCCESS;
    }

    //获取成功的对象，包括数据，状态码默认
    private  ServerResponse(T data){
        this.status=200;
        this.data=data;
    }
    //获取成功的对象，包括状态码和数据
    private  ServerResponse(Integer status,T data){
        this.status=status;
        this.data=data;
    }

    //获取成功的对象，包括状态码和数据，和成功信息
    private  ServerResponse(Integer status,T data,String msg){
        this.status=status;
        this.data=data;
        this.msg=msg;
    }

    //获取失败的对象，包括失败信息，状态码默认
    private  ServerResponse(String msg){
        this.status=100;
        this.msg=msg;
    }

    //获取失败的对象，包括状态码和失败信息
    private  ServerResponse(Integer status,String msg){
        this.status=status;
        this.msg=msg;
    }


    public static <T>ServerResponse successRS(T data){
        return new ServerResponse(data);
    }

    public static <T>ServerResponse successRS(Integer status,T data){
        return new ServerResponse(status,data);
    }

    public static <T>ServerResponse successRS(Integer status,T data,String msg){
        return new ServerResponse(status,data,msg);
    }

    public static <T>ServerResponse defeatedRS(String msg){
        return new ServerResponse(Const.DEFEATED,msg);
    }

    public static <T>ServerResponse defeatedRS(Integer status,String msg){
        return new ServerResponse(status,msg);
    }
}
