package college.processs.module;

import college.processs.enums.ResponseCode;

import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2021/3/9
 * Time: 16:52
 * Version:V1.0
 */
public class Response<T> {

    private int code;

    private String message;

    private T obj;

    public Response(int code, String message, T obj) {
        this.code = code;
        this.message = message;
        this.obj = obj;
    }

    public Response() {
    }

    public Response(Exception ex) {
        this.code = 500;
        this.message = ex.getMessage();
    }

    public Response(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Response(ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMsg();
    }

    public Response(ResponseCode responseCode, T obj) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMsg();
        this.obj = obj;
    }

    public static <T> Response<T> error(ResultState state) {
        return new Response(state, (Object)null);
    }

    public static <T> Response<T> error(int code, String msg) {
        Response response = new Response();
        response.setCode(code);
        response.setMessage(msg);
        return response;
    }

    public Response(ResultState state, final T obj) {
        this.code = state.code();
        this.message = state.message();
        this.obj = obj;
    }


    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getObj() {
        return this.obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}
