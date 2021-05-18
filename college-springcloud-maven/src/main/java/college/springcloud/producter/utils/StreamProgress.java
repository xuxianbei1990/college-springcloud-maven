package college.springcloud.producter.utils;

/**
 * @author: xuxianbei
 * Date: 2021/5/18
 * Time: 14:36
 * Version:V1.0
 */
public interface StreamProgress {
    /**
     * 开始
     */
    void start();

    /**
     * 进行中
     * @param progressSize 已经进行的大小
     */
    void progress(long progressSize);

    /**
     * 结束
     */
    void finish();
}
