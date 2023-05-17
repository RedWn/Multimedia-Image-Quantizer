import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Vector;

public class Main {
    public static void main(String[] args) throws IOException {
        int nColors = 32;
        BufferedImage myPicture = ImageIO.read(new File("test2.jpg"));
        Pixel[] newImage = LloydsAlgorithm.GetQuantizedPixels(ImageManipulation.ImageToMatrix(myPicture), 4);
        ImageIO.write(ImageManipulation.MatrixToImage(newImage, myPicture), "jpg", new File("new-test.jpg"));

        writeIndexed(newImage, myPicture.getWidth(), myPicture.getHeight(), nColors, new File("output.rii"));

    }

    public static void writeIndexed(Pixel[] image,int width,int height,int nColors,File file) throws IOException {
        String fileName = "output.rii";
        writeBinaryStringToFile(padder(888,16),fileName);
        writeBinaryStringToFile(padder(width,16),fileName);
        writeBinaryStringToFile(padder(height,16),fileName);

        int[] colorsRepetition = new int[nColors];
        Vector<Integer> colors = new Vector<Integer>();
        for (int i=0;i<image.length;i++){
            int color = ImageManipulation.convertFromRGBArray(image[i].RGB);
            if (colors.contains(color)){
                colorsRepetition[colors.indexOf(color)]++;
                writeBinaryStringToFile(padder(colors.indexOf(color),8),fileName);
            }
            else {
                colors.add(color);
                writeBinaryStringToFile(padder(colors.indexOf(color),8),fileName);
            }
        }
        for (Integer color : colors){
            writeBinaryStringToFile(padder(colors.indexOf(color),8),fileName);
            writeBinaryStringToFile(padder(color,8),fileName);
            writeBinaryStringToFile(padder(colorsRepetition[colors.indexOf(color)],8),fileName);
        }
    }

    public static String padder(int number, int length) {
        String string = Integer.toBinaryString(number);
        return "0".repeat(Math.max(0, length - string.length())) + string;
    }

    public static void writeBinaryStringToFile(String binaryString, String fileName) throws IOException {
        int value = Integer.parseInt(binaryString, 2);
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(fileName))) {
            dos.writeInt(value);
        }
    }
}