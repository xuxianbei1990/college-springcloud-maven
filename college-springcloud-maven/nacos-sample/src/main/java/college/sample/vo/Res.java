package college.sample.vo;


/**
 * @author lizhejin
 */
public class Res<T> extends Response<T> {

    public Res(int code, String message) {
        this.setCode(code);
        this.setMessage(message);
    }

    public Res(Integer errorCode, T data) {
        this.setCode(errorCode);
        this.setMessage("success");
        this.setObj(data);
    }


    public static <T> Res<T> ok(T data) {
        return new Res(200, data);
    }
}
