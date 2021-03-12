package college.codegenerate.module;

/**
 * @author: xuxianbei
 * Date: 2021/3/10
 * Time: 10:12
 * Version:V1.0
 */
public enum ResultCode implements IResultCode {
    SUCCESS(0, "操作成功"),
    FAIL(-1, "业务异常");

    final int code;
    final String message;

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    private ResultCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }
}