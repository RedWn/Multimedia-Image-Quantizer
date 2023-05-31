package com.ite.multimediaencyclopediagui.images;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
                if (areColorsClose(ImageUtils.RGBtoCIELAB(searchColor.RGB), ImageUtils.RGBtoCIELAB(color.RGB))) {
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
            if (areColorsClose(ImageUtils.RGBtoCIELAB(colors.elementAt(i).RGB),ImageUtils.RGBtoCIELAB(colors.elementAt(index).RGB),5))
                ans += percentages.elementAt(i);
        }
        return ans;
    }

    public static void setTarget(File imageToSearchFor, int nColors) throws IOException {
        //a good question is should we choose the wanted colors automatically or should we give the user the choice?
        //I have no evidence this will make a difference if you are using more than 1 color!
//        BufferedImage BI = ImageIO.read(imageToSearchFor);
//        IOIndexed.writeIndexed(BI,"temp.rii");
        IndexedImage II = IOIndexed.readIndexedImageFromDisk(imageToSearchFor.getAbsolutePath());
//        IndexedImage II = IOIndexed.convertImageToIndexed(BI);
//        IndexedImage II = IOIndexed.readIndexedImageFromDisk(imageToSearchFor.getAbsolutePath());
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
}
