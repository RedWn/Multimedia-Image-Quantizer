import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException {
        int nColors = 16;

//        File ff = new File("./tests");
//            int i=0;
//        for (File f: Objects.requireNonNull(ff.listFiles())) {
//            BufferedImage myPicture = ImageIO.read(f);
//            Pixel[] newImage = MedianCut.Algorithm(ImageManipulation.ImageToMatrix(myPicture), nColors);
//            IOIndexed.writeIndexed(ImageManipulation.MatrixToImage(newImage, myPicture.getWidth(),myPicture.getHeight(),myPicture.getType()), String.format("./riitests/output(%d).rii", i++));
//        }


//        for (int i=0;i<22;i++){
//            IndexedImage newImage2 = IOIndexed.readIndexed(String.format("./riitests/output(%d).rii", i));
//            ImageIO.write(ImageManipulation.MatrixToImage(newImage2.pixels, newImage2.width,newImage2.height,5), "jpg", new File(String.format("output/new-test(%d).jpg", i)));
//        }

//        Pixel[] newImage = LloydsAlgorithm.GetQuantizedPixels(ImageManipulation.ImageToMatrix(myPicture), nColors);
//        IndexedImage newImage = IOIndexed.readIndexed("output.rii");
//        ImageIO.write(ImageManipulation.MatrixToImage(newImage, myPicture.getWidth(), myPicture.getHeight(), myPicture.getType()), "jpg", new File("new-test2.jpg"));

//        IndexedImage newImage2 = IOIndexed.readIndexed("output2.rii");
//        ImageIO.write(ImageManipulation.MatrixToImage(newImage2.pixels, newImage2.width,newImage2.height,myPicture.getType()), "jpg", new File("new-test2.jpg"));



        Searcher.setTarget("./riitests/output(16).rii",3);
        Searcher.color = 5f;
        Searcher.per = 0.005f;
        File[] fff = Searcher.Search("./riitests");
        int i=0;
        for (File ff: fff){
            IndexedImage newImage2 = IOIndexed.readIndexed(ff.getAbsolutePath());
            ImageIO.write(ImageManipulation.MatrixToImage(newImage2.pixels, newImage2.width,newImage2.height,5), "jpg", new File(String.format("search/new-test(%d).jpg", i++)));
        }

    }
}