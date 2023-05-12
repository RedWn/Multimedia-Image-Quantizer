package com.ite.multimediaencyclopediagui.images;

import com.ite.multimediaencyclopediagui.images.Pixel;

import java.util.Arrays;
import java.util.Comparator;

public class MedianCut {

    public static int[] avgRGB(Pixel[] image) {
        int[] avg = new int[3];
        for (Pixel pixel : image) {
            avg[0] += pixel.RGB[0];
            avg[1] += pixel.RGB[1];
            avg[2] += pixel.RGB[2];
        }
        avg[0] /= image.length;
        avg[1] /= image.length;
        avg[2] /= image.length;
        return avg;
    }
    public static Pixel[] Algorithm(Pixel[] image, int nColors) {
        int deg = (int) (Math.log(nColors) / Math.log(2));
        image = SortByColor(image);
        if (deg == 0) {
            int[] temp = avgRGB(image);
            for (Pixel pixel : image) {
                pixel.RGB = temp;
            }
            return image;
        }
        Pixel[] ans = new Pixel[image.length];
        Pixel[] temp = Algorithm(Arrays.copyOfRange(image, 0, image.length / 2), nColors / 2);
        int temp2 = temp.length;
        System.arraycopy(temp, 0, ans, 0, temp.length);
        temp = Algorithm(Arrays.copyOfRange(image, image.length / 2, image.length), nColors / 2);
        System.arraycopy(temp, 0, ans, temp2, temp.length);
        return ans;
    }

    public static Pixel[] SortByColor(Pixel[] image) {
        int[] temp = new int[image.length];
        for (int i = 0; i < image.length; i++) {
            temp[i] = image[i].RGB[0];
        }
        Arrays.sort(temp);
        int dR = temp[temp.length - 1] - temp[0];

        temp = new int[image.length];
        for (int i = 0; i < image.length; i++) {
            temp[i] = image[i].RGB[1];
        }
        Arrays.sort(temp);
        int dG = temp[temp.length - 1] - temp[0];

        temp = new int[image.length];
        for (int i = 0; i < image.length; i++) {
            temp[i] = image[i].RGB[2];
        }
        Arrays.sort(temp);
        int dB = temp[temp.length - 1] - temp[0];

        int temp2 = Math.max(dR, dG);
        temp2 = Math.max(temp2, dB);
        if (temp2 == dB) {
            Arrays.sort(image, Comparator.comparingInt(o -> o.RGB[2]));
        } else if (temp2 == dG) {
            Arrays.sort(image, Comparator.comparingInt(o -> o.RGB[1]));
        } else {
            Arrays.sort(image, Comparator.comparingInt(o -> o.RGB[0]));
        }
        return image;
    }
}
