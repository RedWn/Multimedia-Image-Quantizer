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

        Pixel[] firstQuantizedHalf = GetQuantizedPixels(Arrays.copyOfRange(sortedImagePixels, 0, sortedImagePixels.length / 2), nColors / 2);
        Pixel[] secondQuantizedHalf =  GetQuantizedPixels(Arrays.copyOfRange(sortedImagePixels, sortedImagePixels.length / 2, sortedImagePixels.length), nColors / 2);

        System.arraycopy(firstQuantizedHalf, 0, ans, 0, firstQuantizedHalf.length);
        System.arraycopy(secondQuantizedHalf, 0, ans, firstQuantizedHalf.length, secondQuantizedHalf.length);

        return ans;
    }

    private static int[] getAverageRGB(Pixel[] imagePixels) {
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
        int tempMinRed = Integer.MAX_VALUE;
        int tempMinGreen = Integer.MAX_VALUE;
        int tempMinBlue = Integer.MAX_VALUE;

        int tempMaxRed = Integer.MIN_VALUE;
        int tempMaxGreen = Integer.MIN_VALUE;
        int tempMaxBlue = Integer.MIN_VALUE;

        int redDifference, blueDifference, greenDifference;

        // Calculate difference between max and min components for rgb values.
        for (int i = 0; i < imagePixels.length; i++) {
            tempMaxRed = Math.max(tempMaxRed, imagePixels[i].RGB[0]);
            tempMaxGreen = Math.max(tempMaxGreen, imagePixels[i].RGB[1]);
            tempMaxBlue = Math.max(tempMaxBlue, imagePixels[i].RGB[2]);

            tempMinRed = Math.min(tempMinRed, imagePixels[i].RGB[0]);
            tempMinGreen = Math.min(tempMinGreen, imagePixels[i].RGB[1]);
            tempMinBlue = Math.min(tempMinBlue, imagePixels[i].RGB[2]);
        }

        redDifference = tempMaxRed - tempMinRed;
        greenDifference = tempMaxGreen - tempMinGreen;
        blueDifference = tempMaxBlue - tempMinBlue;

        // Get the max difference out of these rgb differences
        int maxDifference = Math.max(redDifference, greenDifference);
        maxDifference = Math.max(maxDifference, blueDifference);

        // Sort original image pixels according to the color component with the max difference
        int colorIndexToCompareAgainst;

        if (maxDifference == blueDifference) {
            colorIndexToCompareAgainst = 2;
        } else if (maxDifference == greenDifference) {
            colorIndexToCompareAgainst = 1;
        } else {
            colorIndexToCompareAgainst = 0;
        }

        Arrays.sort(imagePixels, Comparator.comparingInt(o -> o.RGB[colorIndexToCompareAgainst]));
        return imagePixels;
    }
}
