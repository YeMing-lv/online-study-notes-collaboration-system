package com.osnc.main.common;

import lombok.Data;
import lombok.ToString;

/*
* 使用 ResultStatus 枚举类
根据您提供的代码，我可以看到`Result`类定义了一个通用的返回结果对象，包含了状态码、消息和数据。
* 它还提供了几个静态方法来快速创建不同类型的结果对象，如成功、失败等。
这个`Result`类看起来很简单且符合常见的返回结果设计，没有明显的错误或问题。
*
* 这个版本的 Result 类提供了更全面的静态方法来创建不同类型的成功和失败结果对象，
* 涵盖了多种情况，包括自定义状态码、消息和数据。这样，你可以根据需要灵活地创建 Result 对象。
* */
@ToString
@Data
public class Result<T>{

    /**
     * 返回的状态码
     */
    private Integer code;
    /**
     * 返回的消息
     */
    private String message;
    /**
     * 返回的数据
     */
    private T data;

    /* 构造方法属性赋值 get获取 */
    public Result(ResultStatus resultStatus, T data){
        this.code = resultStatus.getCode();
        this.message = resultStatus.getMessage();
        this.data = data;
    }

    /* 使用自定义状态码、消息和数据的构造方法
     * @param code
     * @param message
     * @param data
     */
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    /**
     * 返回成功，还有返回数据
     */
    public static <T> Result success(T data){
        //调用构造方法  进行属性赋值  给到该类的属性  属性就有值了
        return  new Result<>(ResultStatus.SUCCESS,data);
    }

    /**
     * 返回成功,自定义状态，并且返回数据（即 可以自定义枚举类里面的 是否成功 同时传递数据）
     * @param resultStatus
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> success(ResultStatus resultStatus,T data){
        return  new Result<T>(resultStatus,data);
    }

    /* 返回带有消息和数据的成功信息
     * @param message
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultStatus.SUCCESS.getCode(), message, data);
    }

     /* 返回成功，带有自定义状态码、消息和数据
     * @param code
     * @param message
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> success(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }

    /**
     * 直接返回服务器内部错误的失败信息
     * @return
     */
    //public static Result failure(){
    //    return  new Result(ResultStatus.SERVER_ERROR,null);
    //}
    public static <T> Result<T> failure(){
        return  new Result<T>(ResultStatus.SERVER_ERROR,null);
    }

    /**
     * 返回带有数据的失败信息
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> failure(T data){
        return  new Result<T>(ResultStatus.SERVER_ERROR,data);
    }

     /* 返回失败，带有消息
     * @param message
     * @return
     */
    public static Result<?> failure(String message) {
        return new Result<>(ResultStatus.SERVER_ERROR.getCode(), message, null);
    }

    /* 返回失败，带有消息和数据
     * @param message
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> failure(String message, T data) {
        return new Result<>(ResultStatus.SERVER_ERROR.getCode(), message, data);
    }


    /* 返回自定义状态的失败信息
     * @param resultStatus
     * @param <T>
     * @return
     */
    public static <T> Result<T> failure(ResultStatus resultStatus) {
        return new Result<>(resultStatus, null);
    }

    /* 返回自定义状态码和消息的失败信息
     * @param code
     * @param message
     * @return
     */
    public static Result<?> failure(Integer code, String message) {
        return new Result<>(code, message, null);
    }

     /* 返回自定义状态码、消息和数据信息的失败信息
     * @param code
     * @param message
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> failure(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }

    /**
     * 返回自定义的失败的状态的数据信息
     * @param resultStatus
     * @param data
     * @param <T>
     * @return
     */
    public static  <T> Result<T> failure(ResultStatus resultStatus,T data){
        return  new Result<>(resultStatus,data);
    }

    /**
     * 判断操作是否成功
     * @return 如果操作成功返回true，否则返回false
     */
    public boolean isSuccess() {
        return code == ResultStatus.SUCCESS.getCode();
    }

}
