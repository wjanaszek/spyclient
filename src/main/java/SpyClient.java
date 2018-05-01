import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Collectors;

@Slf4j
public class SpyClient {
    BufferedImage bufferedImage;
    ServerSocket taskSocket;

    static final String SERVER_URL = "localhost";
    static final int CLIENT_PORT = 8081;
    static final int SERVER_PORT = 9999;

    public static void main(String... args) {
        SpyClient client = new SpyClient();
        try {
            client.taskSocket = new ServerSocket(CLIENT_PORT);
            Socket serverMessage = client.taskSocket.accept();
            log.debug("Get new message from: " + serverMessage.getRemoteSocketAddress());
            String result = new BufferedReader(new InputStreamReader(serverMessage.getInputStream()))
                    .lines()
                    .collect(Collectors.joining("\n"));
            if (result.equals("SPH")) {
                log.info("Get SPH message, sending photo to server");
                client.sendImage(SERVER_URL, SERVER_PORT, "/Users/wjanaszek/Downloads/26772171_759476057591382_693966661_o.jpg");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
