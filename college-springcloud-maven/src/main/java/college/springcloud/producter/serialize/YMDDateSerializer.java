package college.springcloud.producter.serialize;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 年月日序列化
 * @author: xuxianbei
 * Date: 2020/9/9
 * Time: 15:17
 * Version:V1.0
 * 解决阿里fastJon全局序列化和JSONField冲突的问题
 */
public class YMDDateSerializer implements ObjectSerializer {
    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        if (object instanceof Date) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String text = format.format((Date) object);
            serializer.write(text);
        }
    }
}
