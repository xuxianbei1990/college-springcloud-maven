package college.codegenerate.module;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: xuxianbei
 * Date: 2021/3/10
 * Time: 10:11
 * Version:V1.0
 */
@Data
public class R<T> implements Serializable {
    public static final String SUCCESS_MESSAGE = "操作成功";
    public static final String FAILURE_MESSAGE = "业务异常";
    private static final long serialVersionUID = 1L;
    private int code;
    private T obj;
    private String message;



    private R(IResultCode resultCode, T data) {
        this(resultCode, data, resultCode.getMessage());
    }

    private R(IResultCode resultCode, T data, String msg) {
        this(resultCode.getCode(), data, msg);
    }

    private R(int code, T obj, String message) {
        this.code = code;
        this.obj = obj;
        this.message = message;
    }

    public static <T> R<T> ok() {
        return new R(ResultCode.SUCCESS, "操作成功");
    }

    public static <T> R<T> ok(String msg) {
        return new R(ResultCode.SUCCESS, msg);
    }

    public static <T> R<T> ok(T data) {
        return data(data, "操作成功");
    }

    public static <T> R<T> data(T data) {
        return data(data, "操作成功");
    }

    public static <T> R<T> data(T data, String msg) {
        return data(ResultCode.SUCCESS.code, data, msg);
    }

    public static <T> R<T> data(int code, T data, String msg) {
        return new R(code, data, data == null ? "操作成功" : msg);
    }



    public static <T> R<T> ok(IResultCode resultCode, String msg) {
        return new R(resultCode, msg);
    }

    public static <T> R<T> fail() {
        return new R(ResultCode.FAIL, "业务异常");
    }

    public static <T> R<T> fail(String msg) {
        return new R(ResultCode.FAIL, msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        return new R(code, (Object) null, msg);
    }

    public static <T> R<T> fail(IResultCode resultCode, String msg) {
        return new R(resultCode, msg);
    }

    public static <T> R<T> status(boolean flag) {
        return flag ? ok("操作成功") : fail("业务异常");
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof R)) {
            return false;
        } else {
            R<?> other = (R) o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getCode() != other.getCode()) {
                return false;
            } else {
                Object this$obj = this.getObj();
                Object other$obj = other.getObj();
                if (this$obj == null) {
                    if (other$obj != null) {
                        return false;
                    }
                } else if (!this$obj.equals(other$obj)) {
                    return false;
                }

                Object this$message = this.getMessage();
                Object other$message = other.getMessage();
                if (this$message == null) {
                    if (other$message != null) {
                        return false;
                    }
                } else if (!this$message.equals(other$message)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof R;
    }


    public int getCode() {
        return this.code;
    }

    public T getObj() {
        return this.obj;
    }

    public String getMessage() {
        return this.message;
    }

    public void setCode(final int code) {
        this.code = code;
    }

    public void setObj(final T obj) {
        this.obj = obj;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String toString() {
        return "R(code=" + this.getCode() + ", obj=" + this.getObj() + ", message=" + this.getMessage() + ")";
    }

    public R() {
    }
}