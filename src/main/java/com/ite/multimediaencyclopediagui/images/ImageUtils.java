package com.ite.multimediaencyclopediagui.images;

import javafx.scene.image.Image;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.IntBuffer;

public class ImageUtils {
    public static Pixel convertRGBValueToPixel(int rgbValue) {
//        String rgbHexString = Integer.toHexString(rgbValue);
//        Pixel ans = new Pixel();
//        for (int i = 2; i < rgbHexString.length(); i += 2) {
//            String temp = rgbHexString.substring(i, i + 2);
//            ans.RGB[(i - 2) / 2] = Integer.parseInt(temp, 16);
//        }
//        return ans;

        // Bitwise manipulation is faster and more efficient according to ChatGPT
        Pixel ans = new Pixel();
        int red = (rgbValue >> 16) & 0xff;
        int green = (rgbValue >> 8) & 0xff;
        int blue = rgbValue & 0xff;

        ans.RGB = new int[]{red, green, blue};
        return ans;
    }

    public static int convertRGBArrayToValue(int[] rgbArray) {
        return new Color(rgbArray[0], rgbArray[1], rgbArray[2]).getRGB();
    }

    public static Pixel[] ImageToPixels(BufferedImage BI) {
        int imageSize = BI.getHeight() * BI.getWidth();
        Pixel[] ans = new Pixel[imageSize];
        for (int i = 0; i < imageSize; i++) {
            ans[i] = convertRGBValueToPixel(BI.getRGB(i % BI.getWidth(), i / BI.getWidth()));
            ans[i].index = i;
        }
        return ans;
    }

    public static BufferedImage PixelsToImage(Pixel[] imagePixels, int width, int height, int type) {
        BufferedImage ans = new BufferedImage(width, height, type);
        for (Pixel pixel : imagePixels) {
            ans.setRGB(pixel.index % ans.getWidth(), pixel.index / ans.getWidth(), convertRGBArrayToValue(pixel.RGB));
        }
        return ans;
    }

    // https://stackoverflow.com/questions/30970005/bufferedimage-to-javafx-image
    public static Image ConvertBufferedImageToImage(BufferedImage img) {
        //converting to a good type, read about types here: https://openjfx.io/javadoc/13/javafx.graphics/javafx/scene/image/PixelBuffer.html
        BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
        newImg.createGraphics().drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);

        //converting the BufferedImage to an IntBuffer
        int[] type_int_agrb = ((DataBufferInt) newImg.getRaster().getDataBuffer()).getData();
        IntBuffer buffer = IntBuffer.wrap(type_int_agrb);

        //converting the IntBuffer to an Image, read more about it here: https://openjfx.io/javadoc/13/javafx.graphics/javafx/scene/image/PixelBuffer.html
        PixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbPreInstance();
        PixelBuffer<IntBuffer> pixelBuffer = new PixelBuffer(newImg.getWidth(), newImg.getHeight(), buffer, pixelFormat);
        return new WritableImage(pixelBuffer);
    }

    public static double[] RGBtoCIELAB(int[] RGB) {
        double[] xyz = new double[3];
        double[] ans = new double[3];
        double[][] M = {
                {0.412456, 0.357576, 0.180437},
                {0.212673, 0.715152, 0.072175},
                {0.019334, 0.119193, 0.950304}
        };

        for (int i = 0; i < 3; i++) {
            xyz[i] = (float) RGB[i] / 255;
            if (xyz[i] < 0.04045)
                xyz[i] /= xyz[i];
            else
                xyz[i] = Math.pow((xyz[i] + 0.055) / 1.055, 2.4);
        }
        xyz = multiplyMatrices(M, xyz);

        ans[0] = 116 * f(xyz[1] / 100) - 16;
        ans[1] = 500 * (f(xyz[0] / 95.0489) - f(xyz[1] / 100));
        ans[2] = 200 * (f(xyz[1] / 100) - f(xyz[2] / 108.884));

        return ans;
    }

    static double f(double t) {
        if (t > 0.008856)
            return Math.pow(t, 1f / 3f);
        else
            return 7.787 * t + 16f / 116;
    }

    static double[] multiplyMatrices(double[][] a, double[] b) {
        int rowsInA = a.length;
        int columnsInA = a[0].length;
        double[] result = new double[rowsInA];
        for (int i = 0; i < rowsInA; i++) {
            for (int k = 0; k < columnsInA; k++) {
                result[i] += a[i][k] * b[k];
            }
        }
        return result;
    }

    /**
     * <p>
     * Gets the base name, without extension, of given file name.
     * <p/>
     * e.g. getBaseName("file.txt") will return "file"
     */
    public static String getFileBaseName(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return fileName;
        } else {
            return fileName.substring(0, index);
        }
    }
}
