package com.ite.multimediaencyclopediagui.images;

import java.util.List;
import java.util.Random;
import java.util.Vector;

public class LloydsAlgorithm {
    public static Pixel[] GetQuantizedPixels(Pixel[] imagePixels, int nColors) {
        int[][] kMeans = new int[nColors][];
        Vector<Pixel>[] kGroups = new Vector[nColors];
        boolean done = false;
        Pixel[] ans = new Pixel[imagePixels.length];
        int[] avg = getGroupCenter(new Vector<>(List.of(imagePixels)));
        Random rand = new Random();
        for (int i = 0; i < kMeans.length; i++) {
            int[] temp = new int[3];
            for (int j = 0; j < temp.length; j++) {
                temp[j] = (rand.nextInt(256) + avg[j] - 128);
            }
            kMeans[i] = temp;
        }
        while (!done) {
            done = true;
            for (int i = 0; i < kGroups.length; i++) {
                kGroups[i] = new Vector<>();
            }
            int group = 0;
            for (Pixel imagePixel : imagePixels) {
                int minD = Integer.MAX_VALUE;
                for (int j = 0; j < nColors; j++) {
                    if (distanceBetweenPoints(imagePixel.RGB, kMeans[j]) < minD) {
                        minD = distanceBetweenPoints(imagePixel.RGB, kMeans[j]);
                        group = j;
                    }
                }
                kGroups[group].add(imagePixel);
            }
            for (int i = 0; i < kGroups.length; i++) {
                int[] center;
                if (kGroups[i].size() == 0) {
                    center = kMeans[i];
                } else {
                    center = getGroupCenter(kGroups[i]);
                }
                if (!sameVector(center, kMeans[i])) {
                    kMeans[i] = center;
                    done = false;
                }
            }
        }
        int i = 0;
        for (int j = 0; j < kGroups.length; j++) {
            for (Pixel pixel : kGroups[j]) {
                ans[i] = new Pixel();
                ans[i].index = pixel.index;
                ans[i++].RGB = kMeans[j];
            }
        }
        return ans;
    }

    public static int distanceBetweenPoints(int[] a, int[] b) {
        return (int) Math.sqrt(Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2) + Math.pow(a[2] - b[2], 2));
    }

    public static int[] getGroupCenter(Vector<Pixel> group) {
        int[] ans = new int[3];
        for (Pixel pixel : group) {
            ans[0] += pixel.RGB[0];
            ans[1] += pixel.RGB[1];
            ans[2] += pixel.RGB[2];
        }
        ans[0] /= group.size();
        ans[1] /= group.size();
        ans[2] /= group.size();
        return ans;
    }

    public static boolean sameVector(int[] a, int[] b) {
        return a[0] == b[0] && a[1] == b[1] && a[2] == b[2];
    }
}