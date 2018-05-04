import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

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

            // use register, login needs id
            client.register("test", "test");
            // client should fetch userID
            // client.login(userID, "test2");

            while (true) {
                int length = dIn.readInt();
                byte[] header = new byte[length];
                dIn.readFully(header, 0, length);
                String result = new String(header, StandardCharsets.UTF_8);
                log.debug("Get new message from: " + SERVER_URL);
                if (result.equals(Header.PHOTO_REQUEST.toString())) {
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
            dos.write(Header.PHOTO.getValue().getBytes(StandardCharsets.UTF_8));
            dos.write(fileContent);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void register(String login, String password) {
        try {
            DataOutputStream dos = new DataOutputStream(taskSocket.getOutputStream());
            // we are adding 2 ints (because we are sending 2 fields except header - login and password), so we have to extend our message by 2 * 4
            dos.writeInt(3 + login.getBytes(StandardCharsets.UTF_8).length + password.getBytes(StandardCharsets.UTF_8).length + 2 * 4);
            dos.write(Header.REGISTER.getValue().getBytes(StandardCharsets.UTF_8));
            // before every field we have to add its length, otherwise parser could not read that
            dos.writeInt(login.getBytes(StandardCharsets.UTF_8).length);
            dos.write(login.getBytes(StandardCharsets.UTF_8));
            // before every field we have to add its length, otherwise parser could not read that
            dos.writeInt(password.getBytes(StandardCharsets.UTF_8).length);
            dos.write(password.getBytes(StandardCharsets.UTF_8));
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void login(int login, String password) {
        try {
            DataOutputStream dos = new DataOutputStream(taskSocket.getOutputStream());
            // we are adding 2 ints (because we are sending 2 fields except header - login and password), so we have to extend our message by 2 * 4
            dos.writeInt(3 + 4 + password.getBytes(StandardCharsets.UTF_8).length + 4);
            dos.write(Header.AUTHENTICATE.getValue().getBytes(StandardCharsets.UTF_8));
            // int field does not have length field
            dos.writeInt(login);
            // before every field we have to add its length, otherwise parser could not read that
            dos.writeInt(password.getBytes(StandardCharsets.UTF_8).length);
            dos.write(password.getBytes(StandardCharsets.UTF_8));
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
