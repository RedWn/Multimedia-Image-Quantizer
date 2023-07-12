package com.ite.multimediaencyclopediagui.search;

import com.ite.multimediaencyclopediagui.images.Algorithms.MedianCutAlgorithm;
import com.ite.multimediaencyclopediagui.images.IOIndexed;
import com.ite.multimediaencyclopediagui.images.ImageUtils;
import com.ite.multimediaencyclopediagui.images.IndexedImage;
import com.ite.multimediaencyclopediagui.images.Pixel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Searcher {
    public static float colorThreshold = 5f;
    public static float percentageThreshold = 0.005f;
    public static int sizeThreshold = 100000;
    public static int nDays = 7;
    public static int loadingNumber = 0;
    static long dateThreshold = 86400 * nDays;
    static IndexedImage targetImage = null;
    static ArrayList<SearchColor> colors;
    static int targetSize;
    static long targetDate;

    public static File[] Search(String directory, boolean ByColor, boolean ByDate, boolean BySize) throws IOException {
        File file = new File(directory);
        File[] ans = file.listFiles();
        if (ByDate) {
            ans = SearchByDate(ans);
        }
        if (BySize) {
            ans = SearchBySize(ans);
        }
        if (ByColor) {
            ans = SearchByColor(ans);
        }
        return ans;
    }

    public static File[] SearchByColor(File[] files) throws IOException {
        ArrayList<File> ans = new ArrayList<>();

        for (File F : Objects.requireNonNull(files)) {
            DataInputStream dis = new DataInputStream(new FileInputStream(F.getAbsoluteFile()));
            if (dis.readShort() == 888) {
                if (isColorSuitable(F)) {
                    ans.add(F);
                }
            }
            loadingNumber++;
        }
        File[] ansArray = new File[ans.size()];
        for (int i = 0; i < ansArray.length; i++) {
            ansArray[i] = ans.get(i);
        }
        return ansArray;
    }

    public static File[] SearchByDate(File[] files) throws IOException {
        dateThreshold = 86400 * nDays;
        ArrayList<File> ans = new ArrayList<>();

        for (File F : Objects.requireNonNull(files)) {
            DataInputStream dis = new DataInputStream(new FileInputStream(F.getAbsoluteFile()));
            if (dis.readShort() == 888) {
                if (isDateSuitable(F)) {
                    ans.add(F);
                }
                else
                    System.out.println(F.getAbsoluteFile());
            }
            loadingNumber++;
        }
        File[] ansArray = new File[ans.size()];
        for (int i = 0; i < ansArray.length; i++) {
            ansArray[i] = ans.get(i);
        }
        return ansArray;
    }

    public static File[] SearchBySize(File[] files) throws IOException {
        ArrayList<File> ans = new ArrayList<>();

        for (File F : Objects.requireNonNull(files)) {
            DataInputStream dis = new DataInputStream(new FileInputStream(F.getAbsoluteFile()));
            if (dis.readShort() == 888) {
                if (isSizeSuitable(F)) {
                    ans.add(F);
                }
            }
            loadingNumber++;
        }
        File[] ansArray = new File[ans.size()];
        for (int i = 0; i < ansArray.length; i++) {
            ansArray[i] = ans.get(i);
        }
        return ansArray;
    }

    static boolean isColorSuitable(File F) throws IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream(F.getAbsoluteFile()));
        int locks = colors.size();
        dis.readShort();
        int width = dis.readShort();
        int height = dis.readShort();
        dis.skipBytes(width * height);
        boolean getOut = false;
        int buffer;
        ArrayList<Pixel> colors = new ArrayList<>();
        ArrayList<Float> percentages = new ArrayList<>();
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
                    if (arePercentagesClose(searchColor.percentage, getAllSimilarColorsPercentages(colors, percentages, colors.indexOf(color))))
                        locks--;
                }
            }
        }
        return locks <= 0;
    }

    static boolean isDateSuitable(File F) throws IOException {
        BasicFileAttributes attr = Files.readAttributes(F.toPath(), BasicFileAttributes.class);
        System.out.println(attr.creationTime().toMillis()/1000);
        long temp = Math.abs(targetDate - attr.creationTime().toMillis()/1000);
        if (temp < dateThreshold)
            return true;
        return false;
    }

    static boolean isSizeSuitable(File F) throws IOException {
        DataInputStream sourceImageData = new DataInputStream(new FileInputStream(F));
        sourceImageData.readShort();

        int sourceImageWidth = sourceImageData.readShort();
        int sourceImageHeight = sourceImageData.readShort();
        int tempSize = sourceImageWidth * sourceImageHeight;
        if (Math.abs(targetSize - tempSize) < sizeThreshold)
            return true;
        return false;
    }

    static boolean areColorsClose(double[] c1, double[] c2) {
        return Math.sqrt(Math.pow(c1[0] - c2[0], 2) + Math.pow(c1[1] - c2[1], 2) + Math.pow(c1[2] - c2[2], 2)) < colorThreshold;
    }

    static boolean areColorsClose(double[] c1, double[] c2, double threshold) {
        return Math.sqrt(Math.pow(c1[0] - c2[0], 2) + Math.pow(c1[1] - c2[1], 2) + Math.pow(c1[2] - c2[2], 2)) < threshold;
    }

    static boolean arePercentagesClose(float c1, float c2) {
        return Math.abs(c1 - c2) < percentageThreshold;
    }

    static float getAllSimilarColorsPercentages(ArrayList<Pixel> colors, ArrayList<Float> percentages, int index) {
        float ans = 0;
        for (int i = 0; i < colors.size(); i++) {
            if (areColorsClose(ImageUtils.RGBtoCIELAB(colors.get(i).RGB), ImageUtils.RGBtoCIELAB(colors.get(index).RGB), 5))
                ans += percentages.get(i);
        }
        return ans;
    }

    public static void setColorTarget(File imageToSearchFor, int nColors) throws IOException {
        BufferedImage BI = ImageIO.read(imageToSearchFor);
        targetImage = IOIndexed.convertImageToIndexed(ImageUtils.PixelsToImage(MedianCutAlgorithm.GetQuantizedPixels(ImageUtils.ImageToPixels(BI), 64), BI.getWidth(), BI.getHeight(), BI.getType()), "temp.rii");
        int maxIndex = 0;

        ArrayList<Integer> taken = new ArrayList<>();

        while (nColors != 0) {
            float max = Float.MIN_VALUE;
            for (int i = 0; i < targetImage.colorPercentage.length; i++) {
                if (taken.contains(i))
                    continue;
                if (max < getAllSimilarColorsPercentages(new ArrayList<>(List.of(targetImage.colors)), new ArrayList<>(List.of(targetImage.colorPercentage)), i)) {
                    max = getAllSimilarColorsPercentages(new ArrayList<>(List.of(targetImage.colors)), new ArrayList<>(List.of(targetImage.colorPercentage)), i);
                    maxIndex = i;
                }
            }
            taken.add(maxIndex);
            nColors--;
        }
        colors = new ArrayList<>();
        ArrayList<Pixel> temp = new ArrayList<>(List.of(targetImage.colors));
        ArrayList<Float> temp2 = new ArrayList<>(List.of(targetImage.colorPercentage));
        for (int index : taken) {
            colors.add(new SearchColor(targetImage.colors[index].RGB, getAllSimilarColorsPercentages(temp, temp2, index)));
        }
    }

    public static void setDateTarget(File imageToSearchFor) throws IOException {
        BasicFileAttributes attr = Files.readAttributes(imageToSearchFor.toPath(), BasicFileAttributes.class);
        targetDate = attr.creationTime().toMillis()/1000;
    }

    public static void setSizeTarget(File imageToSearchFor) throws IOException {
        BufferedImage BI = ImageIO.read(imageToSearchFor);
        targetSize = BI.getWidth() * BI.getHeight();
    }
}
