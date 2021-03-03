package college.sample.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2021/3/2
 * Time: 16:27
 * Version:V1.0
 */
public class Response<T> {
    @ApiModelProperty(
            position = 1,
            value = "返回码"
    )
    private int code;
    @ApiModelProperty(
            position = 2,
            value = "返回信息"
    )
    private String message;
    @ApiModelProperty(
            position = 3,
            value = "返回体"
    )
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

