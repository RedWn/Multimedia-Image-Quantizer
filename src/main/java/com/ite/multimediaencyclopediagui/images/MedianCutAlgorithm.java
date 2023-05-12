package com.ite.multimediaencyclopediagui.images;

import java.util.Arrays;
import java.util.Comparator;

public class MedianCutAlgorithm {
    public static Pixel[] GetQuantizedPixels(Pixel[] imagePixels, int nColors) {
        int deg = (int) (Math.log(nColors) / Math.log(2));
        Pixel[] sortedImagePixels = sortPixelsByColor(imagePixels);
        if (deg == 0) {
            int[] temp = getAverageRGB(sortedImagePixels);
            for (Pixel pixel : sortedImagePixels) {
                pixel.RGB = temp;
            }
            return sortedImagePixels;
        }
        Pixel[] ans = new Pixel[sortedImagePixels.length];
        Pixel[] temp = GetQuantizedPixels(Arrays.copyOfRange(sortedImagePixels, 0, sortedImagePixels.length / 2), nColors / 2);
        int temp2 = temp.length;
        System.arraycopy(temp, 0, ans, 0, temp.length);
        temp = GetQuantizedPixels(Arrays.copyOfRange(sortedImagePixels, sortedImagePixels.length / 2, sortedImagePixels.length), nColors / 2);
        System.arraycopy(temp, 0, ans, temp2, temp.length);
        return ans;
    }

    public static int[] getAverageRGB(Pixel[] imagePixels) {
        int[] avg = new int[3];
        for (Pixel pixel : imagePixels) {
            avg[0] += pixel.RGB[0];
            avg[1] += pixel.RGB[1];
            avg[2] += pixel.RGB[2];
        }
        avg[0] /= imagePixels.length;
        avg[1] /= imagePixels.length;
        avg[2] /= imagePixels.length;
        return avg;
    }

    private static Pixel[] sortPixelsByColor(Pixel[] imagePixels) {
        int[] temp = new int[imagePixels.length];
        for (int i = 0; i < imagePixels.length; i++) {
            temp[i] = imagePixels[i].RGB[0];
        }
        Arrays.sort(temp);
        int dR = temp[temp.length - 1] - temp[0];

        temp = new int[imagePixels.length];
        for (int i = 0; i < imagePixels.length; i++) {
            temp[i] = imagePixels[i].RGB[1];
        }
        Arrays.sort(temp);
        int dG = temp[temp.length - 1] - temp[0];

        temp = new int[imagePixels.length];
        for (int i = 0; i < imagePixels.length; i++) {
            temp[i] = imagePixels[i].RGB[2];
        }
        Arrays.sort(temp);
        int dB = temp[temp.length - 1] - temp[0];

        int temp2 = Math.max(dR, dG);
        temp2 = Math.max(temp2, dB);
        if (temp2 == dB) {
            Arrays.sort(imagePixels, Comparator.comparingInt(o -> o.RGB[2]));
        } else if (temp2 == dG) {
            Arrays.sort(imagePixels, Comparator.comparingInt(o -> o.RGB[1]));
        } else {
            Arrays.sort(imagePixels, Comparator.comparingInt(o -> o.RGB[0]));
        }
        return imagePixels;
    }
}
