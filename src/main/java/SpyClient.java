import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

@Slf4j
public class SpyClient {
    BufferedImage bufferedImage;

    static final String SERVER_URL = "localhost";
    static final int PORT = 8000;

    public static void main(String... args) {
        SpyClient client = new SpyClient();
        client.sendImage(SERVER_URL, PORT, "/Users/wjanaszek/Downloads/26772171_759476057591382_693966661_o.jpg");
    }

    BufferedImage loadImage(String path) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    void sendImage(String url, int port, String path) {
        try {
            Socket socket = new Socket(url, port);
            bufferedImage = this.loadImage(path);
            ImageIO.write(bufferedImage, path.split(".")[0], socket.getOutputStream());
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
