package com.jing.libjpegcompress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * author: 陈永镜 .
 * date: 2017/7/26 .
 * email: jing20071201@qq.com
 * <p>
 * introduce:
 * skia图片处理引擎采用哈弗曼编码
 * 哈夫曼编码：
 * 需要扫描整个信息(图片信息--每个像素),
 * 通过变长码(bitmap使用定长码)来计算每个字符出现的频率
 */
public class CompressUtils {

    static {
        System.loadLibrary("jpegbither");
        System.loadLibrary("compress_utils");
    }

    /**
     * 使用哈夫曼编码对图片进行压缩处理
     *
     * @param bitmap
     * @param filePath
     */
    public static void compressJpegToFile(Bitmap bitmap, String filePath) {
        int options = 20;

        saveBitmap(bitmap, options, filePath, true);
    }

    /**
     * @param bitmap   需要压缩的图片
     * @param options  压缩的比例
     * @param filePath 压缩后需要保存到本地的路径
     * @param optimize 是否需要使用哈夫曼编码
     */
    private static void saveBitmap(Bitmap bitmap, int options, String filePath, boolean optimize) {
        compressBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), options, filePath.getBytes(), optimize);
    }

    public static native String compressBitmap(Bitmap bitmap, int w, int h, int quality, byte[] fileNameBytes, boolean optimize);


    /**
     * 质量压缩
     *
     * @param bitmap
     * @param file   将bitmap压缩成文件保存
     *               <p>
     *               原理：通过算法扣掉图片中的一些点附近相近的像素，达到降低质量减少文件大小的目的
     *               <p>
     *               注意：只是降低file的质量，对加载这个图片出来的bitmap内存是没有影响的
     *               因为bitmap在内存中的大小是按照像素计算的(宽X高)，对于质量压缩，并不会改变图片的真是像素
     *               <p>
     *               使用场景：
     *               将图片压缩后保存到本地或上传到服务器
     */
    public static void compressImageToFile(Bitmap bitmap, File file) {

        int quality = 10;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 尺寸压缩
     *
     * @param bitmap
     * @param file   原理：通过减少单位尺寸的像素值，降低像素
     *               <p>
     *               使用场景：
     *               缓存缩略图或头像
     */
    public static void compressBitmapToFile(Bitmap bitmap, File file) {

        // 尺寸压缩倍数,值越大，图片尺寸越小
        int ratio = 8;
        // 压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap(bitmap.getWidth() / ratio, bitmap.getHeight() / ratio, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, bitmap.getWidth() / ratio, bitmap.getHeight() / ratio);
        canvas.drawBitmap(bitmap, null, rect, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        result.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 采样率压缩
     *
     * @param filePath     原图片的路径
     * @param file         图片压缩后保存的路径
     * @param inSampleSize 图片压缩的采样率
     */
    public static void compressSampleToFile(String filePath, File file, int inSampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;

        //采样率
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //将压缩后的数据存到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        try {
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
