package college.springcloud.producter.utils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.IORuntimeException;


import cn.hutool.core.util.ObjectUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author: xuxianbei
 * Date: 2021/5/18
 * Time: 13:34
 * Version:V1.0
 */
public class MyZipUtil {

    /**
     * 默认编码，使用平台相关编码
     */
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    /**
     * 默认缓存大小 8192
     */
    public static final int DEFAULT_BUFFER_SIZE = 2 << 12;

    /**
     * 数据流末尾
     */
    public static final int EOF = -1;

    /**
     * 对流中的数据加入到压缩文件<br>
     * 路径列表和流列表长度必须一致
     *
     * @param zipFile 生成的Zip文件，包括文件名。注意：zipPath不能是srcPath路径下的子文件夹
     * @param paths   流数据在压缩文件中的路径或文件名
     * @param ins     要压缩的源，添加完成后自动关闭流
     * @return 压缩文件
     * @throws UtilException IO异常
     * @since 3.0.9
     */
    public static File zip(File zipFile, String[] paths, InputStream[] ins) throws UtilException {
        return zip(zipFile, paths, ins, DEFAULT_CHARSET);
    }

    /**
     * 对流中的数据加入到压缩文件<br>
     * 路径列表和流列表长度必须一致
     *
     * @param zipFile 生成的Zip文件，包括文件名。注意：zipPath不能是srcPath路径下的子文件夹
     * @param paths   流数据在压缩文件中的路径或文件名
     * @param ins     要压缩的源，添加完成后自动关闭流
     * @param charset 编码
     * @return 压缩文件
     * @throws UtilException IO异常
     * @since 3.0.9
     */
    public static File zip(File zipFile, String[] paths, InputStream[] ins, Charset charset) throws UtilException {
        ZipOutputStream out = null;
        try {
            out = getZipOutputStream(zipFile, charset);
            zip(out, paths, ins);
        } finally {
            close(out);
        }
        return zipFile;
    }

    /**
     * 获得 {@link ZipOutputStream}
     *
     * @param zipFile 压缩文件
     * @param charset 编码
     * @return {@link ZipOutputStream}
     */
    private static ZipOutputStream getZipOutputStream(File zipFile, Charset charset) {
        return getZipOutputStream(getOutputStream(zipFile), charset);
    }

    /**
     * 创建文件夹，会递归自动创建其不存在的父文件夹，如果存在直接返回此文件夹<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param dir 目录
     * @return 创建的目录
     */
    public static File mkdir(File dir) {
        if (dir == null) {
            return null;
        }
        if (false == dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 创建所给文件或目录的父目录
     *
     * @param file 文件或目录
     * @return 父目录
     */
    public static File mkParentDirs(File file) {
        if (null == file) {
            return null;
        }
        return mkdir(file.getParentFile());
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param file 文件对象
     * @return 文件，若路径为null，返回null
     * @throws IORuntimeException IO异常
     */
    public static File touch(File file) throws IORuntimeException {
        if (null == file) {
            return null;
        }
        if (false == file.exists()) {
            mkParentDirs(file);
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }
        return file;
    }

    /**
     * 获得一个输出流对象
     *
     * @param file 文件
     * @return 输出流对象
     * @throws IORuntimeException IO异常
     */
    public static BufferedOutputStream getOutputStream(File file) throws IORuntimeException {
        final OutputStream out;
        try {
            out = new FileOutputStream(touch(file));
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        return toBuffered(out);
    }

    /**
     * 转换为{@link BufferedOutputStream}
     *
     * @param out {@link OutputStream}
     * @return {@link BufferedOutputStream}
     * @since 4.0.10
     */
    public static BufferedOutputStream toBuffered(OutputStream out) {
        cn.hutool.core.lang.Assert.notNull(out, "OutputStream must be not null!");
        return (out instanceof BufferedOutputStream) ? (BufferedOutputStream) out : new BufferedOutputStream(out);
    }

    /**
     * 获得 {@link ZipOutputStream}
     *
     * @param out     压缩文件流
     * @param charset 编码
     * @return {@link ZipOutputStream}
     */
    private static ZipOutputStream getZipOutputStream(OutputStream out, Charset charset) {
        if (out instanceof ZipOutputStream) {
            return (ZipOutputStream) out;
        }
        return new ZipOutputStream(out, ObjectUtil.defaultIfNull(charset, DEFAULT_CHARSET));
    }


    /**
     * 数组是否为空
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 是否为空
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 将文件流压缩到目标流中
     *
     * @param zipOutputStream 目标流，压缩完成不关闭
     * @param paths           流数据在压缩文件中的路径或文件名
     * @param ins             要压缩的源，添加完成后自动关闭流
     * @throws IORuntimeException IO异常
     * @since 5.5.2
     */
    public static void zip(ZipOutputStream zipOutputStream, String[] paths, InputStream[] ins) throws IORuntimeException {
        if (isEmpty(paths) || isEmpty(ins)) {
            throw new IllegalArgumentException("Paths or ins is empty !");
        }
        if (paths.length != ins.length) {
            throw new IllegalArgumentException("Paths length is not equals to ins length !");
        }
        for (int i = 0; i < paths.length; i++) {
            add(ins[i], paths[i], zipOutputStream);
        }
    }

    /**
     * 添加文件流到压缩包，添加后关闭流
     *
     * @param in   需要压缩的输入流，使用完后自动关闭
     * @param path 压缩的路径
     * @param out  压缩文件存储对象
     * @throws IORuntimeException IO异常
     */
    private static void add(InputStream in, String path, ZipOutputStream out) throws IORuntimeException {
        if (null == in) {
            return;
        }
        try {
            out.putNextEntry(new ZipEntry(path));
            copy(in, out);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            close(in);
            closeEntry(out);
        }
    }

    /**
     * 拷贝流，使用默认Buffer大小，拷贝后不关闭流
     *
     * @param in  输入流
     * @param out 输出流
     * @return 传输的byte数
     * @throws IORuntimeException IO异常
     */
    public static long copy(InputStream in, OutputStream out) throws IORuntimeException {
        return copy(in, out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 拷贝流，拷贝后不关闭流
     *
     * @param in         输入流
     * @param out        输出流
     * @param bufferSize 缓存大小
     * @return 传输的byte数
     * @throws IORuntimeException IO异常
     */
    public static long copy(InputStream in, OutputStream out, int bufferSize) throws IORuntimeException {
        return copy(in, out, bufferSize, null);
    }

    /**
     * 拷贝流，拷贝后不关闭流
     *
     * @param in             输入流
     * @param out            输出流
     * @param bufferSize     缓存大小
     * @param streamProgress 进度条
     * @return 传输的byte数
     * @throws IORuntimeException IO异常
     */
    public static long copy(InputStream in, OutputStream out, int bufferSize, StreamProgress streamProgress) throws IORuntimeException {
        Assert.notNull(in, "InputStream is null !");
        Assert.notNull(out, "OutputStream is null !");
        if (bufferSize <= 0) {
            bufferSize = DEFAULT_BUFFER_SIZE;
        }

        byte[] buffer = new byte[bufferSize];
        if (null != streamProgress) {
            streamProgress.start();
        }
        long size = 0;
        try {
            for (int readSize; (readSize = in.read(buffer)) != EOF; ) {
                out.write(buffer, 0, readSize);
                size += readSize;
                if (null != streamProgress) {
                    streamProgress.progress(size);
                }
            }
            out.flush();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        if (null != streamProgress) {
            streamProgress.finish();
        }
        return size;
    }

    /**
     * 关闭<br>
     * 关闭失败不会抛出异常
     *
     * @param closeable 被关闭的对象
     */
    public static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                // 静默关闭
            }
        }
    }

    /**
     * 关闭当前Entry，继续下一个Entry
     *
     * @param out ZipOutputStream
     */
    private static void closeEntry(ZipOutputStream out) {
        try {
            out.closeEntry();
        } catch (IOException e) {
            // ignore
        }
    }

    public static void downloadExcleByZip(String fileName, HttpServletResponse response, List<Workbook> workbooks) {
        setHeader(response, fileName);

        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream(), DEFAULT_CHARSET);

            for (int i = 0; i < workbooks.size(); i++) {
                Workbook workbook = workbooks.get(i);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                workbook.write(baos);
                baos.close();
                add(new ByteArrayInputStream(baos.toByteArray()), fileName + i + ".xls", zipOutputStream);
                workbook.close();
            }
            zipOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setHeader(HttpServletResponse response, String fileName) {

        /**
         *  浏览器处理乱码问题
         *  String userAgent = request.getHeader("User-Agent");
         *  filename.getBytes("UTF-8")处理safari的乱码问题
         *  byte[] bytes = userAgent.contains("MSIE") ? fileName.getBytes() : fileName.getBytes("UTF-8");
         *  各浏览器基本都支持ISO编码
         *  fileName = new String(bytes, "ISO-8859-1");
         */

        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/zip");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(null, "test"), CfInvoiceCommonExportVo.class, new ArrayList<>());
        String[] ps = {"1.xls"};
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(
                new File("new.zip")));
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream, DEFAULT_CHARSET);

        List<InputStream> ins = new ArrayList();
        ins.add(new ByteArrayInputStream(baos.toByteArray()));
        for (int i = 0; i < ins.size(); i++) {
            add(ins.get(i), "test" + i + ".xls", zipOutputStream);
        }
        zipOutputStream.close();
        bufferedOutputStream.close();
//        standard(ps, baos);
    }

    private static void standard(String[] ps, ByteArrayOutputStream baos) {
        InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
        InputStream[] inputStreams = new InputStream[]{inputStream};
        zip(new File("testZip.zip"), ps, inputStreams);
    }
}
