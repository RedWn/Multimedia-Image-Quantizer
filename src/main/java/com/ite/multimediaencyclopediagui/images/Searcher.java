package com.ite.multimediaencyclopediagui.images;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Vector;

public class Searcher {
    public static int colorThreshold = 7;
    public static float percentageThreshold = 0.0001f;
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
            float percentage = dis.readFloat();
            for (SearchColor color : colors
            ) {
                if (areColorsClose(temp, color.RGB) && arePercentagesClose(percentage, color.percentage)) {
                    locks--;
                }
            }
        }
        return locks == 0;
    }

    static boolean areColorsClose(int[] c1, int[] c2) {
        return Math.sqrt(Math.pow(c1[0] - c2[0], 2) + Math.pow(c1[1] - c2[1], 2) + Math.pow(c1[2] - c2[2], 2)) < colorThreshold;
    }

    static boolean arePercentagesClose(float c1, float c2) {
        return Math.abs(c1 - c2) < percentageThreshold;
    }

    public static void setTarget(String fileDirectory, int nColors) throws IOException {
        //TODO: needs a major rework for it to support multiple color search
        //a good question is should we choose the wanted colors automatically or should we give the user the choice?
        File file = new File(fileDirectory);
        IndexedImage II = IOIndexed.readIndexedWithPercentages(file.getAbsolutePath());
        float max = Float.MIN_VALUE;
        int maxIndex = 0;
        for (int i = 0; i < II.colorPercentage.length; i++) {
            if (max > II.colorPercentage[i]) {
                max = II.colorPercentage[i];
                maxIndex = i;
            }
        }
        colors = new Vector<>();
        colors.add(new SearchColor(II.colors[maxIndex].RGB, II.colorPercentage[maxIndex]));
    }
}
