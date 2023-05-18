package com.ite.multimediaencyclopediagui.images;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Vector;

public class IOIndexed {
    public static void writeIndexed(BufferedImage BI, int nColors, String fileName) throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(fileName));
        dos.writeShort(888);
        dos.writeShort(BI.getWidth());
        dos.writeShort(BI.getHeight());

        int[] colorsRepetition = new int[nColors];
        Vector<Integer> colors = new Vector<>();
        for (int i = 0; i < BI.getWidth()*BI.getHeight(); i++) {
            int color = BI.getRGB(i%BI.getWidth(),i/BI.getWidth());
            if (colors.contains(color)) {
                colorsRepetition[colors.indexOf(color)]++;
            } else {
                colors.add(color);
            }
            dos.writeByte(colors.indexOf(color));
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

    public static IndexedImage readIndexed(String fileName) throws IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream(fileName));
        int buffer;
        if (dis.readShort() != 888) {
            return new IndexedImage();
        }
        int width = dis.readShort();
        int height = dis.readShort();
        IndexedImage ans = new IndexedImage();
        ans.pixels = new Pixel[width * height];
        Vector<Pixel> colors = new Vector<>();
        dis.skipBytes(ans.pixels.length);
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
        for (int i = 0; i < ans.pixels.length; i++) {
            Pixel temp = new Pixel();
            temp.index = i;
            buffer = dis.readByte();
            temp.RGB = colors.elementAt(buffer).RGB;
            ans.pixels[i] = temp;
        }
        ans.width = width;
        ans.height = height;
        return ans;
    }
}
