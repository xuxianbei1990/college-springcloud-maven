package college.processs.enums;

/**
 * @author: xuxianbei
 * Date: 2021/3/9
 * Time: 16:53
 * Version:V1.0
 */
public enum ResponseCode {
    SUCCESS(200, "success"),
    FAIL(401, "error"),
    SAVE_SUCCESS(200, "保存成功"),
    EXPORT_SUCCESS(200, "导出成功"),
    UPDATE_SUCCESS(200, "编辑成功"),
    BATCH_DELETE_SUCCESS(200, "批量删除成功"),
    SAVE_ERROR(500, "保存失败"),
    DELETE_SUCCESS(200, "删除成功"),
    DELETE_ERROR(500, "删除失败"),
    NOT_FIND_DATABASE_CONN(401, "not find database conn"),
    LOGIN_SUCCESS(200, "登录成功"),
    LOGIN_ERROR(401, "登录失败"),
    LOGIN_EASY(500, "登录密码过于简单"),
    PARAMETER_ERROR(403, "参数错误"),
    SAMPLE_STOCK_HANDOVER_ERROR(400, "此样衣正在转交中"),
    STOCKOUT_ERROR(400, "库存不足，请修改后提交"),
    DRAFT_PRINT_EXIST(500, "存在已打印数据"),
    DRAFT_PRINT_ID_ERROR(500, "画稿不存在");

    private final int code;
    private final String msg;

    private ResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
