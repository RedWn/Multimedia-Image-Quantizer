package com.ite.multimediaencyclopediagui.images;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Vector;

public class IOIndexed {
    /**
     * This isn't a special value: just a simple integer to mark the indexed images that we're writing.
     * If this value isn't present when reading an image, then we cannot process it.
     */
    private static final short REDWAN_INDEXED_IMAGE_MARKER = 888;
    private static final short END_OF_FILE_MARKER = -1;

    /**
     * Indexed image format:
     * - Special Marker
     * - Width
     * - Height
     * - [...colorIndices]
     * - Colors map: [rgb percentageOfColorInImage, rgb percentageOfColorInImage]
     */
    public static void writeIndexed(BufferedImage BI, String fileName) throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(fileName));

        // Mark this file as written by us.
        dos.writeShort(REDWAN_INDEXED_IMAGE_MARKER);

        dos.writeShort(BI.getWidth());
        dos.writeShort(BI.getHeight());

        // Since we're reading only quantized images, the number of total colors
        // cannot exceed 256
        int[] colorOccurrences = new int[256];

        // key: color RGB value (int)
        // value: index (int)
        HashMap<Integer, Integer> colorsMap = new HashMap();
        int incrementalColorIndex = 0;

        for (int i = 0; i < BI.getWidth() * BI.getHeight(); i++) {
            int colorRGBValue = BI.getRGB(i % BI.getWidth(), i / BI.getWidth());

            if (colorsMap.containsKey(colorRGBValue)) {
                int colorIndex = colorsMap.get(colorRGBValue);
                colorOccurrences[colorIndex]++;
            } else {
                colorsMap.put(colorRGBValue, incrementalColorIndex);
                incrementalColorIndex++;
            }

            dos.writeByte(colorsMap.get(colorRGBValue));
        }

        colorsMap.forEach((colorRGBValue, colorIndexInMap) -> {
            try {
                Pixel pixel = ImageUtils.convertRGBToPixel(colorRGBValue);
                dos.writeShort(pixel.RGB[0]);
                dos.writeShort(pixel.RGB[1]);
                dos.writeShort(pixel.RGB[2]);

                // percentage ==  (number of times the color was repeated) / (number of total pixels)
                float colorPercentageInMap = (float) (colorOccurrences[colorIndexInMap]) / (BI.getWidth() * BI.getHeight());
                dos.writeFloat(colorPercentageInMap);
            } catch (Exception e) {

            }
        });

        dos.writeShort(END_OF_FILE_MARKER);
    }

    //probably deprecated
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
            dis.readFloat();
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

    public static IndexedImage readIndexedWithPercentages(String fileName) throws IOException {
        DataInputStream sourceImageData = new DataInputStream(new FileInputStream(fileName));

        // We cannot process this image since it's not written by us
        if (sourceImageData.readShort() != REDWAN_INDEXED_IMAGE_MARKER) {
            return new IndexedImage();
        }

        int sourceImageWidth = sourceImageData.readShort();
        int sourceImageHeight = sourceImageData.readShort();

        IndexedImage finalImage = new IndexedImage();
        finalImage.width = sourceImageWidth;
        finalImage.height = sourceImageHeight;
        finalImage.pixels = new Pixel[sourceImageWidth * sourceImageHeight];

        // TODO: Convert to actual map
        Vector<Pixel> colorsMap = new Vector<>();
        Vector<Float> colorPercentageMap = new Vector<>();

        // Skip all color indices and reach for the actual colors map which contains rgb values
        sourceImageData.skipBytes(finalImage.pixels.length);

        boolean getOut = false;
        while (true) {
            Pixel temp = new Pixel();
            for (int j = 0; j < 3; j++) {
                int colorComponent = sourceImageData.readShort();
                if (colorComponent == END_OF_FILE_MARKER) {
                    getOut = true;
                    break;
                }
                temp.RGB[j] = colorComponent;
            }

            if (getOut) break;

            colorsMap.add(temp);

            float colorPercentage = sourceImageData.readFloat();
            colorPercentageMap.add(colorPercentage);
        }

        sourceImageData = new DataInputStream(new FileInputStream(fileName));

        // Skip special marker + width + height and reach for color indices
        sourceImageData.skipBytes(6);

        for (int i = 0; i < finalImage.pixels.length; i++) {
            Pixel temp = new Pixel();
            int colorIndex = sourceImageData.readByte();

            temp.index = i; // TODO: Why? Shouldn't this be temp.index = colorIndex ?
            temp.RGB = colorsMap.elementAt(colorIndex).RGB;

            finalImage.pixels[i] = temp;
        }

        finalImage.colors = new Pixel[colorsMap.size()];

        for (int i = 0; i < colorsMap.size(); i++) {
            finalImage.colors[i] = colorsMap.elementAt(i);
        }

        finalImage.colorPercentage = new Float[colorPercentageMap.size()];

        for (int i = 0; i < colorPercentageMap.size(); i++) {
            finalImage.colorPercentage[i] = colorPercentageMap.elementAt(i);
        }

        return finalImage;
    }
}
