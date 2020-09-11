package college.springcloud.producter.mybatis;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * @author: xuxianbei
 * Date: 2020/9/10
 * Time: 16:51
 * Version:V1.0
 */
public class UpdateBatchById extends AbstractMethod {
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        final String sql = "<script>UPDATE %s %s %s %s</script>";
        String prepresql = prepareField(tableInfo);
        String preId = prepareId(tableInfo);
        String resultsql = String.format(sql, tableInfo.getTableName(),
                prepresql, "where", preId);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, resultsql, modelClass);
        return this.addUpdateMappedStatement(mapperClass, modelClass, "batchUpdateById", sqlSource);
    }

    private String prepareId(TableInfo tableInfo) {
        StringBuilder sql = new StringBuilder("<foreach collection=\"list\" separator=\"or\" item=\"item\" index=\"index\">");
        sql.append(String.format("%s=#{item.%s}", tableInfo.getKeyColumn(), tableInfo.getKeyProperty())).append("</foreach>");
        return sql.toString();
    }

    private String prepareField(TableInfo tableInfo) {
        StringBuilder fieldSql = new StringBuilder();
        fieldSql.append("<trim prefix= \"set\" suffixOverrides=\",\">");
        tableInfo.getFieldList().forEach(field -> {
            fieldSql.append(String.format("<trim prefix=\"%s=case\" suffix=\"end,\">", field.getColumn()));
            fieldSql.append("<foreach collection=\"list\" item=\"item\" index=\"index\">");
            fieldSql.append(String.format("<if test=\"item.%s!=null\">", field.getProperty()));
            fieldSql.append(String.format("when %s=#{item.%s} then #{item.%s}",
                    tableInfo.getKeyColumn(), tableInfo.getKeyProperty(), field.getProperty()));
            fieldSql.append("</if> </foreach> </trim>");
        });
        fieldSql.append("</trim>");
        return fieldSql.toString();
    }
}
