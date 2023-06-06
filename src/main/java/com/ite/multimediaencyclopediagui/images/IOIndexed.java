package com.ite.multimediaencyclopediagui.images;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
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
    public static void convertImageToIndexedAndWriteToDisk(BufferedImage BI, String fileName) throws IOException {
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

            if (!colorsMap.containsKey(colorRGBValue)) {
                colorsMap.put(colorRGBValue, incrementalColorIndex);
                incrementalColorIndex++;
            }
            int colorIndex = colorsMap.get(colorRGBValue);
            colorOccurrences[colorIndex]++;

            dos.writeByte(colorsMap.get(colorRGBValue));
        }

        for (int i=0;i<colorsMap.size();i++){
            int colorIndexInMap = getKeyFromValue(colorsMap,i); //this solution is shit, but I don't see another way
            Pixel pixel = ImageUtils.convertRGBValueToPixel(colorIndexInMap);
            dos.writeShort(pixel.RGB[0]);
            dos.writeShort(pixel.RGB[1]);
            dos.writeShort(pixel.RGB[2]);
            float colorPercentageInMap = (float) (colorOccurrences[i]) / (BI.getWidth() * BI.getHeight());
            dos.writeFloat(colorPercentageInMap);
        }
        dos.writeShort(END_OF_FILE_MARKER);
        dos.close();
    }

    public static IndexedImage readIndexedImageFromDisk(String fileName) throws IOException {
        DataInputStream sourceImageData = new DataInputStream(new FileInputStream(fileName));
        DataInputStream sourceImageDataForSecondPass = new DataInputStream(new FileInputStream(fileName));

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

        boolean hasReachedEndOfFile = false;

        while (true) {
            Pixel temp = new Pixel();
            for (int j = 0; j < 3; j++) {
                int colorComponent = sourceImageData.readShort();
                if (colorComponent == END_OF_FILE_MARKER) {
                    hasReachedEndOfFile = true;
                    break;
                }
                temp.RGB[j] = colorComponent;
            }

            if (hasReachedEndOfFile) break;

            colorsMap.add(temp);

            float colorPercentage = sourceImageData.readFloat();
            colorPercentageMap.add(colorPercentage);
        }

        sourceImageData.close();

        // Skip special marker + width + height and reach for color indices
        sourceImageDataForSecondPass.skipBytes(6);

        for (int i = 0; i < finalImage.pixels.length; i++) {
            Pixel temp = new Pixel();
            temp.index = i; //this is the index of the pixel in the context of the image

            int colorIndex = sourceImageDataForSecondPass.readUnsignedByte();
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

    public static <K, V> K getKeyFromValue(HashMap<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

}
