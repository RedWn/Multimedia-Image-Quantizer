package com.ite.multimediaencyclopediagui.images;

import javafx.scene.image.Image;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.IntBuffer;

public class ImageUtils {
    private static Pixel convertRGBToPixel(int rgbValue) {
        String rgbHexString = Integer.toHexString(rgbValue);
        Pixel ans = new Pixel();
        for (int i = 2; i < rgbHexString.length(); i += 2) {
            String temp = rgbHexString.substring(i, i + 2);
            ans.RGB[(i - 2) / 2] = Integer.parseInt(temp, 16);
        }
        return ans;
    }

    private static int convertFromRGBArray(int[] x) {
        StringBuilder ans = new StringBuilder("ff");
        for (int j : x) {
            String temp = Integer.toHexString(j);
            if (temp.length() < 2) {
                ans.append("0").append(temp);
            } else {
                ans.append(temp);
            }
        }
        return (int) Long.parseLong(ans.toString(), 16);
    }

    public static Pixel[] ImageToPixels(BufferedImage BI) {
        int imageSize = BI.getHeight() * BI.getWidth();
        Pixel[] ans = new Pixel[imageSize];
        for (int i = 0; i < imageSize; i++) {
            ans[i] = convertRGBToPixel(BI.getRGB(i % BI.getWidth(), i / BI.getWidth()));
            ans[i].index = i;
        }
        return ans;
    }

    public static BufferedImage PixelsToImage(Pixel[] imagePixels, BufferedImage res) {
        BufferedImage ans = new BufferedImage(res.getWidth(), res.getHeight(), res.getType());
        for (Pixel pixel : imagePixels) {
            ans.setRGB(pixel.index % ans.getWidth(), pixel.index / ans.getWidth(), convertFromRGBArray(pixel.RGB));
        }
        return ans;
    }

    // https://stackoverflow.com/questions/30970005/bufferedimage-to-javafx-image
    public static Image ConvertBufferedImageToImage(BufferedImage img){
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
}
