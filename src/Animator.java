import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class Animator {
    private String imageFormat = ".png";
    private BufferedImage[] images;
    private int imageCount;
    private double fps = 4d;
    private Logger logger = Logger.getLogger(Animator.class.getName());

    public Animator(String path, int imageCount) throws IOException{
        if(imageCount<1) {
            throw new IOException("Image count must be larger 0");
        }
        images = new BufferedImage[imageCount];
        int broken = 0;
        for (int i = 0; i < imageCount; i++) {
            try {
                String imagePath = path + i + imageFormat;
                images[i] = ImageIO.read(new File(imagePath));
            } catch (IOException e) {
                broken++;
                logger.info("Image missing: path="+imageFormat);
            }
        }
        this.imageCount = imageCount-broken;
        if(this.imageCount==0){
            throw new IOException("No one image found: path=" + path);
        }
    }

    public BufferedImage getCurrentFrame(long timeOffset){
        return images[(int)(timeOffset*fps/1000d)%imageCount];
    }
}
