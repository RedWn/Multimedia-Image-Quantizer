package com.ite.multimediaencyclopediagui.images;

import java.awt.image.BufferedImage;

public class ImageManipulation {
    public static Pixel convertToPixel(int x) {
        String hexString = Integer.toHexString(x);
        Pixel ans = new Pixel();
        for (int i = 2; i < hexString.length(); i += 2) {
            String temp = hexString.substring(i, i + 2);
            ans.RGB[(i - 2) / 2] = Integer.parseInt(temp, 16);
        }
        return ans;
    }

    public static int convertFromRGBArray(int[] x) {
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

    public static Pixel[] ImageToMatrix(BufferedImage BI) {
        int size = BI.getHeight() * BI.getWidth();
        Pixel[] ans = new Pixel[size];
        for (int i = 0; i < size; i++) {
            ans[i] = convertToPixel(BI.getRGB(i % BI.getWidth(), i / BI.getWidth()));
            ans[i].index = i;
        }
        return ans;
    }

    public static BufferedImage MatrixToImage(Pixel[] image, BufferedImage res) {
        BufferedImage ans = new BufferedImage(res.getWidth(), res.getHeight(), res.getType());
        for (Pixel pixel : image) {
            ans.setRGB(pixel.index % ans.getWidth(), pixel.index / ans.getWidth(), convertFromRGBArray(pixel.RGB));
        }
        return ans;
    }
}
