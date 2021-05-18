package college.springcloud.producter.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import college.springcloud.producter.model.SampleVo;
import college.springcloud.producter.utils.CfInvoiceCommonExportVo;
import college.springcloud.producter.utils.MyZipUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author: xuxianbei
 * Date: 2021/5/18
 * Time: 15:02
 * Version:V1.0
 */
@RequestMapping("file")
@RestController
public class FileController {


    @PostMapping("download/excels")
    public void downLoadExcels(HttpServletResponse response) {
        Workbook workbook1 = ExcelExportUtil.exportExcel(new ExportParams(null, "test"), CfInvoiceCommonExportVo.class, new ArrayList<>());
        Workbook workbook2 = ExcelExportUtil.exportExcel(new ExportParams(null, "test"), CfInvoiceCommonExportVo.class, new ArrayList<>());
        Workbook workbook3 = ExcelExportUtil.exportExcel(new ExportParams(null, "test"), CfInvoiceCommonExportVo.class, new ArrayList<>());
        MyZipUtil.downloadExcleByZip("test.zip", response, Arrays.asList(workbook1, workbook2, workbook3));
    }
}
