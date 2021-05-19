package college.multidatasource.utils;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.io.File;

/**
 * @author lizhejin
 * Date: 2019/9/10
 */
public class GeneratorUtil {
    /**
     * 数据库配置四要素
     */
    private static final String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://10.228.81.19:38309/eop_finance_dev?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Chenfan@123.com.cn..";

    public static void main(String[] args) {
        moduleGenerator(new String[]{
//                "advancepay_application",
//                "cf_actual_payment",
//                "cf_charge",
//                "cf_charge_history",
//                "cf_charge_in",
//                "cf_invoice_detail",
//                "cf_invoice_header",
//                "cf_po_detail",
//                "cf_po_header",
                "cf_multy_image",

//                "cf_wdt_rd_record_header",
//                "downpayment_conf",
//                "rule_billing_detail",
//                "rule_billing_header"
        });
    }

    private static void moduleGenerator(String[] tableNames){

        // 全局配置
        GlobalConfig globalConfig = getGlobalConfig();

        // 数据源配置
        DataSourceConfig dataSourceConfig = getDataSourceConfig();

        // 包配置
        PackageConfig packageConfig = getPackageConfig();

        // 策略配置
        StrategyConfig strategyConfig = getStrategyConfig(tableNames);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig
                .setEntity(new TemplateConfig().getEntity(false))
                //mapper模板采用mybatis-plus自己模板
                .setMapper(new TemplateConfig().getMapper())
                .setXml(new TemplateConfig().getXml())
                .setService(new TemplateConfig().getService())
                .setServiceImpl(null)
                .setController(null);

        // 自定义xml生成位置
//        InjectionConfig injectionConfig = getInjectionConfig();

        new AutoGenerator()
                .setGlobalConfig(globalConfig)
                .setDataSource(dataSourceConfig)
                .setPackageInfo(packageConfig)
//                .setCfg(injectionConfig)
                .setStrategy(strategyConfig)
                .setTemplate(templateConfig)
                .setTemplateEngine(new FreemarkerTemplateEngine())
                .execute();

    }

    private static GlobalConfig getGlobalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        String authorName = "xuxianbei";
        globalConfig.setOpen(false)
                //new File(module).getAbsolutePath()得到模块根目录路径，因事Maven项目，代码指定路径自定义调整
                .setOutputDir(new File("multi-datasource/src").getAbsolutePath()+"/main/java")
                //生成文件的输出目录
                .setFileOverride(false)
                //是否覆盖已有文件
                .setAuthor(authorName)
                .setBaseResultMap(true)
                .setBaseColumnList(true)
                .setEnableCache(false)
                .setEntityName("%s")
                .setMapperName("%sMapper")
//                .setServiceName("%sService")
//                .setServiceImplName("%sServiceImpl")
                .setXmlName("%sMapper");
        return globalConfig;
    }

    private static DataSourceConfig getDataSourceConfig() {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.MYSQL)
                .setDriverName(DRIVER_NAME)
                .setUsername(USERNAME)
                .setPassword(PASSWORD)
                .setUrl(URL);
        return dataSourceConfig;
    }

    private static PackageConfig getPackageConfig() {
        PackageConfig packageConfig = new PackageConfig();
        //不同模块 代码生成具体路径自定义指定
        String basePackage = "college.multidatasource";
        packageConfig.setParent(basePackage)
                .setEntity("model")
                .setMapper("dao")
                .setXml("dao.impl")
//                .setService("service")
//                .setServiceImpl("service.impl")
                .setController("controller");
        return packageConfig;
    }

    private static StrategyConfig getStrategyConfig(String[] tableNames) {
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig
                //驼峰命名
                .setCapitalMode(true)
                .setEntityLombokModel(true)
                .setNaming(NamingStrategy.underline_to_camel)
                .setInclude(tableNames);
        return strategyConfig;
    }

}