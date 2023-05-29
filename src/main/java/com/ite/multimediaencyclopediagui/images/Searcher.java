package com.ite.multimediaencyclopediagui.images;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class Searcher {
    public static float colorThreshold = 5f;
    public static float percentageThreshold = 0.005f;
    static Vector<SearchColor> colors;

    public static File[] Search(String directory) throws IOException {
        File FF = new File(directory);
        Vector<File> ans = new Vector<>();
        if (FF.isDirectory()) {
            for (File F : Objects.requireNonNull(FF.listFiles())) {
                DataInputStream dis = new DataInputStream(new FileInputStream(F.getCanonicalFile()));
                if (dis.readShort() == 888) {
                    if (isSuitable(F)) {
                        ans.add(F);
                    }
                }
            }
        }
        File[] ansArray = new File[ans.size()];
        for (int i = 0; i < ansArray.length; i++) {
            ansArray[i] = ans.elementAt(i);
        }
        return ansArray;
    }

    static boolean isSuitable(File F) throws IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream(F.getAbsoluteFile()));
        int locks = colors.size();
        dis.readShort();
        int width = dis.readShort();
        int height = dis.readShort();
        dis.skipBytes(width * height);
        boolean getOut = false;
        int buffer;
        Vector<Pixel> colors = new Vector<>();
        Vector<Float> percentages = new Vector<>();
        while (true) {
            int[] temp = new int[3];
            for (int j = 0; j < 3; j++) {
                buffer = dis.readShort();
                if (buffer == -1) {
                    getOut = true;
                    break;
                }
                temp[j] = buffer;
            }
            if (getOut)
                break;
            Pixel temp2 = new Pixel();
            temp2.RGB = temp;
            colors.add(temp2);
            percentages.add(dis.readFloat());
        }
        for (SearchColor searchColor : Searcher.colors) {
            for (Pixel color : colors) {
                if (areColorsClose(RGBtoCIELAB(searchColor.RGB), RGBtoCIELAB(color.RGB))) {
                    if (arePercentagesClose(searchColor.percentage, getAllSimilarColorsPercentages(colors,percentages,colors.indexOf(color))))
                        locks--;
                }
            }
        }
        return locks <= 0;
    }


    static boolean areColorsClose(double[] c1, double[] c2) {
        return Math.sqrt(Math.pow(c1[0] - c2[0], 2) + Math.pow(c1[1] - c2[1], 2) + Math.pow(c1[2] - c2[2], 2)) < colorThreshold;
    }

    static boolean areColorsClose(double[] c1, double[] c2,double threshold) {
        return Math.sqrt(Math.pow(c1[0] - c2[0], 2) + Math.pow(c1[1] - c2[1], 2) + Math.pow(c1[2] - c2[2], 2)) < threshold;
    }

    static boolean arePercentagesClose(float c1, float c2) {
        return Math.abs(c1 - c2) < percentageThreshold;
    }

    static float getAllSimilarColorsPercentages(Vector<Pixel> colors,Vector<Float> percentages,int index){
        float ans = 0;
        for (int i=0;i<colors.size();i++) {
            if (areColorsClose(RGBtoCIELAB(colors.elementAt(i).RGB),RGBtoCIELAB(colors.elementAt(index).RGB),5))
                ans += percentages.elementAt(i);
        }
        return ans;
    }

    public static void setTarget(String fileDirectory, int nColors) throws IOException {
        //a good question is should we choose the wanted colors automatically or should we give the user the choice?
        //I have no evidence this will make a difference if you are using more than 1 color!
        File file = new File(fileDirectory);
        IndexedImage II = IOIndexed.readIndexedWithPercentages(file.getAbsolutePath());
        int maxIndex = 0;
        Vector<Integer> taken = new Vector<>();
        while (nColors != 0) {
            float max = Float.MIN_VALUE;
            for (int i = 0; i < II.colorPercentage.length; i++) {
                if (taken.contains(i))
                    continue;
                if (max > II.colorPercentage[i]) {
                    max = II.colorPercentage[i];
                    maxIndex = i;
                }
            }
            taken.add(maxIndex);
            nColors--;
        }
        colors = new Vector<>();
        Vector<Pixel> temp = new Vector<>(List.of(II.colors));
        Vector<Float> temp2 = new Vector<>(List.of(II.colorPercentage));
        for (int index : taken) {
            colors.add(new SearchColor(II.colors[index].RGB, getAllSimilarColorsPercentages(temp,temp2,index)));
        }
    }

    static double[] RGBtoCIELAB(int[] RGB) {
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
}
