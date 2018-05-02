import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;

@Slf4j
public class SpyClient {
    Socket taskSocket;

    static final String SERVER_URL = "localhost";
    static final int SERVER_PORT = 9999;

    public static void main(String... args) throws InterruptedException {
        SpyClient client = new SpyClient();
        try {
            client.taskSocket = new Socket(SERVER_URL, SERVER_PORT);
            DataInputStream dIn = new DataInputStream(client.taskSocket.getInputStream());

            while (true) {
                int length = dIn.readInt();
                byte[] header = new byte[length];
                dIn.readFully(header, 0, length);
                String result = new String(header, StandardCharsets.UTF_8);
                log.debug("Get new message from: " + SERVER_URL);
                if (result.equals("SPH")) {
                    Thread.sleep(2000);
                    log.info("Get SPH message, sending photo to server");
                    client.sendImage("/home/michal/Downloads/ted.jpeg");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.taskSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    File loadImage(String path) {
        return new File(path);
    }

    void sendImage(String path) {
        try {
            File file = this.loadImage(path);
            byte[] fileContent = Files.readAllBytes(file.toPath());
            DataOutputStream dos = new DataOutputStream(taskSocket.getOutputStream());
            dos.writeInt(fileContent.length + 3);
            dos.write("PHT".getBytes(StandardCharsets.UTF_8));
            dos.write(fileContent);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
