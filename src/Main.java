import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedImage myPicture = ImageIO.read(new File("test.jpg"));
        Pixel[] newImage = MedianCut.Algorithm(ImageManuplation.ImageToMatrix(myPicture), 16);
        ImageIO.write(ImageManuplation.MatrixToImage(newImage, myPicture), "jpg", new File("new-test.jpg"));
    }
}