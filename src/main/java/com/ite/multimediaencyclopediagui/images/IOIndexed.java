package com.ite.multimediaencyclopediagui.images;

import java.io.*;
import java.util.Vector;

public class IOIndexed {
    public static void writeIndexed(Pixel[] image, int width, int height, int nColors, String fileName) throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(fileName));
        dos.writeShort(888);
        dos.writeShort(width);
        dos.writeShort(height);

        int[] colorsRepetition = new int[nColors];
        Vector<Integer> colors = new Vector<>();
        int[] tempColors = new int[image.length];
        for (Pixel pixel : image) {
            int color = ImageUtils.convertFromRGBArray(pixel.RGB);
            if (colors.contains(color)) {
                colorsRepetition[colors.indexOf(color)]++;
            } else {
                colors.add(color);
            }
            tempColors[pixel.index] = color;
        }
        for (int tempColor : tempColors) {
            dos.writeByte(colors.indexOf(tempColor));
        }
        for (Integer color : colors) {
            Pixel temp = ImageUtils.convertRGBToPixel(color);
            dos.writeShort(temp.RGB[0]);
            dos.writeShort(temp.RGB[1]);
            dos.writeShort(temp.RGB[2]);
            dos.writeShort(colorsRepetition[colors.indexOf(color)]);
        }
        dos.writeShort(-1);
    }

    public static Pixel[] readIndexed(String fileName) throws IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream(fileName));
        int buffer;
        if (dis.readShort() != 888) {
            return new Pixel[0];
        }
        int width = dis.readShort();
        int height = dis.readShort();
        Pixel[] ans = new Pixel[width * height];
        Vector<Pixel> colors = new Vector<>();
        dis.skipBytes(ans.length);
        boolean getOut = false;
        while (true) {
            Pixel temp = new Pixel();
            for (int j = 0; j < 3; j++) {
                buffer = dis.readShort();
                if (buffer == -1) {
                    getOut = true;
                    break;
                }
                temp.RGB[j] = buffer;
            }
            if (getOut)
                break;
            colors.add(temp);
            dis.readShort();
        }
        dis = new DataInputStream(new FileInputStream(fileName));
        dis.skipBytes(6);
        for (int i = 0; i < ans.length; i++) {
            Pixel temp = new Pixel();
            temp.index = i;
            buffer = dis.readByte();
            temp.RGB = colors.elementAt(buffer).RGB;
            ans[i] = temp;
        }
        return ans;
    }
}
