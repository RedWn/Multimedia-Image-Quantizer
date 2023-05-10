import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedImage myPicture = ImageIO.read(new File("test.jpg"));
        Pixel[] newImage = MedianCut(ImageToMatrix(myPicture), 8);
        ImageIO.write(MatrixToImage(newImage, myPicture), "jpg", new File("new-test.jpg"));
    }

    public static Pixel convertToPixel(int x) {
        String hexString = Integer.toHexString(x);
        Pixel ans = new Pixel();
        for (int i = 2; i < hexString.length(); i += 2) {
            String temp = hexString.substring(i, i + 2);
            ans.RGB[(i - 2) / 2] = Integer.parseInt(temp, 16);
        }
        return ans;
    }

    public static int convertFromRGBArray(int[] x) {
        StringBuilder ans = new StringBuilder("ff");
        for (int j : x) {
            String temp = Integer.toHexString(j);
            if (temp.length() < 2) {
                ans.append("0").append(temp);
            } else {
                ans.append(temp);
            }
        }
        return (int) Long.parseLong(ans.toString(), 16);
    }

    public static Pixel[] ImageToMatrix(BufferedImage BI) {
        int size = BI.getHeight() * BI.getWidth();
        Pixel[] ans = new Pixel[size];
        for (int i = 0; i < size; i++) {
            ans[i] = convertToPixel(BI.getRGB(i % BI.getWidth(), i / BI.getWidth()));
            ans[i].index = i;
        }
        return ans;
    }

    public static BufferedImage MatrixToImage(Pixel[] image, BufferedImage res) {
        BufferedImage ans = new BufferedImage(res.getWidth(), res.getHeight(), res.getType());
        for (Pixel pixel : image) {
            ans.setRGB(pixel.index % ans.getWidth(), pixel.index / ans.getWidth(), convertFromRGBArray(pixel.RGB));
        }
        return ans;
    }

    public static int[] avgRGB(Pixel[] image) {
        int[] avg = new int[3];
        for (Pixel pixel : image) {
            avg[0] += pixel.RGB[0];
            avg[1] += pixel.RGB[1];
            avg[2] += pixel.RGB[2];
        }
        avg[0] /= image.length;
        avg[1] /= image.length;
        avg[2] /= image.length;
        return avg;
    }

    public static Pixel[] MedianCut(Pixel[] image, int nColors) {
        int deg = (int) (Math.log(nColors) / Math.log(2));
        image = sorter(image);
        if (deg == 0) {
            int[] temp = avgRGB(image);
            for (Pixel pixel : image) {
                pixel.RGB = temp;
            }
            return image;
        }
        Pixel[] ans = new Pixel[image.length];
        Pixel[] temp = MedianCut(Arrays.copyOfRange(image, 0, image.length / 2), nColors / 2);
        int temp2 = temp.length;
        System.arraycopy(temp, 0, ans, 0, temp.length);
        temp = MedianCut(Arrays.copyOfRange(image, image.length / 2, image.length), nColors / 2);
        System.arraycopy(temp, 0, ans, temp2, temp.length);
        return ans;
    }

    public static Pixel[] sorter(Pixel[] image) {
        Pixel[] ans;

        int[] temp = new int[image.length];
        for (int i = 0; i < image.length; i++) {
            temp[i] = image[i].RGB[0];
        }
        Arrays.sort(temp);
        int dR = temp[temp.length - 1] - temp[0];

        temp = new int[image.length];
        for (int i = 0; i < image.length; i++) {
            temp[i] = image[i].RGB[1];
        }
        Arrays.sort(temp);
        int dG = temp[temp.length - 1] - temp[0];

        temp = new int[image.length];
        for (int i = 0; i < image.length; i++) {
            temp[i] = image[i].RGB[2];
        }
        Arrays.sort(temp);
        int dB = temp[temp.length - 1] - temp[0];

        int temp2 = Math.max(dR, dG);
        temp2 = Math.max(temp2, dB);
        if (temp2 == dB) {
            ans = sortMatrix(image, 2);
        } else if (temp2 == dG) {
            ans = sortMatrix(image, 1);
        } else {
            ans = sortMatrix(image, 0);
        }
        return ans;
    }

    public static Pixel[] sortMatrix(Pixel[] image, int column) {
        Arrays.sort(image, Comparator.comparingInt(o -> o.RGB[column]));
        return image;
    }
}