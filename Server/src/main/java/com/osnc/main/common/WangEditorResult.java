package com.osnc.main.common;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class WangEditorResult<T> {

    /**
     * 返回的状态码
     */
    private Integer errno;
    /**
     * 返回的消息
     */
    private String message;
    /**
     * 返回的数据
     */
    private T data;

    /* 构造方法属性赋值 get获取 */
    public WangEditorResult(ResultStatus resultStatus, T data){
        this.errno = resultStatus.getCode();
        this.message = resultStatus.getMessage();
        this.data = data;
    }

    /* 使用自定义状态码、消息和数据的构造方法
     * @param code
     * @param message
     * @param data
     */
    public WangEditorResult(Integer code, String message, T data) {
        this.errno = code;
        this.message = message;
        this.data = data;
    }
    /**
     * 返回成功，还有返回数据
     */
    public static <T> WangEditorResult success(T data){
        //调用构造方法  进行属性赋值  给到该类的属性  属性就有值了
        return  new WangEditorResult(ResultStatus.SUCCESS,data);
    }

    /**
     * 返回成功,自定义状态，并且返回数据（即 可以自定义枚举类里面的 是否成功 同时传递数据）
     * @param resultStatus
     * @param data
     * @param <T>
     * @return
     */
    public static <T> WangEditorResult<T> success(ResultStatus resultStatus,T data){
        return  new WangEditorResult<T>(resultStatus,data);
    }

    /* 返回带有消息和数据的成功信息
     * @param message
     * @param data
     * @param <T>
     * @return
     */
    public static <T> WangEditorResult<T> success(String message, T data) {
        return new WangEditorResult<T>(ResultStatus.SUCCESS.getCode(), message, data);
    }

    /* 返回成功，带有自定义状态码、消息和数据
     * @param code
     * @param message
     * @param data
     * @param <T>
     * @return
     */
    public static <T> WangEditorResult<T> success(Integer code, String message, T data) {
        return new WangEditorResult<T>(code, message, data);
    }

    /**
     * 直接返回服务器内部错误的失败信息
     * @return
     */
    //public static Result failure(){
    //    return  new Result(ResultStatus.SERVER_ERROR,null);
    //}
    public static <T> WangEditorResult<T> failure(){
        return  new WangEditorResult<>(ResultStatus.SERVER_ERROR,null);
    }

    /**
     * 返回带有数据的失败信息
     * @param data
     * @param <T>
     * @return
     */
    public static <T> WangEditorResult<T> failure(T data){
        return  new WangEditorResult<>(ResultStatus.SERVER_ERROR,data);
    }

    /* 返回失败，带有消息
     * @param message
     * @return
     */
    public static WangEditorResult<?> failure(String message) {
        return new WangEditorResult<>(ResultStatus.SERVER_ERROR.getCode(), message, null);
    }

    /* 返回失败，带有消息和数据
     * @param message
     * @param data
     * @param <T>
     * @return
     */
    public static <T> WangEditorResult<T> failure(String message, T data) {
        return new WangEditorResult<T>(ResultStatus.SERVER_ERROR.getCode(), message, data);
    }


    /* 返回自定义状态的失败信息
     * @param resultStatus
     * @param <T>
     * @return
     */
    public static <T> WangEditorResult<T> failure(ResultStatus resultStatus) {
        return new WangEditorResult<>(resultStatus, null);
    }

    /* 返回自定义状态码和消息的失败信息
     * @param code
     * @param message
     * @return
     */
    public static WangEditorResult<?> failure(Integer code, String message) {
        return new WangEditorResult<>(code, message, null);
    }

    /* 返回自定义状态码、消息和数据信息的失败信息
     * @param code
     * @param message
     * @param data
     * @param <T>
     * @return
     */
    public static <T> WangEditorResult<T> failure(Integer code, String message, T data) {
        return new WangEditorResult<>(code, message, data);
    }

    /**
     * 返回自定义的失败的状态的数据信息
     * @param resultStatus
     * @param data
     * @param <T>
     * @return
     */
    public static  <T> WangEditorResult<T> failure(ResultStatus resultStatus,T data){
        return  new WangEditorResult<>(resultStatus,data);
    }

    /**
     * 判断操作是否成功
     * @return 如果操作成功返回true，否则返回false
     */
    public boolean isSuccess() {
        return errno == ResultStatus.SUCCESS.getCode();
    }
}
