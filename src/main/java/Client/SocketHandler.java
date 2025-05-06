package Client;

import Hardwares.Screens.ScreenDriver;

import java.io.*;
import java.net.Socket;

public class SocketHandler {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    /**
     * Sends the card ID to the server and passes the response to the shared ScreenDriver instance.
     *
     * @return
     */
    public static String sendCardToScreen(String cardId) {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(cardId); // send card

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }

            String finalMessage = response.toString().trim();

            // ✅ Access ScreenDriver singleton and deliver message
            ScreenDriver.getInstance().readExternalMessage("scd" + finalMessage);

        } catch (IOException e) {
            ScreenDriver.getInstance().readExternalMessage("scdError: " + e.getMessage());
        }
        return cardId;
    }
}
