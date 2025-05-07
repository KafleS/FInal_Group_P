package Client;

import Hardwares.Screens.ScreenDriver;

import java.io.*;
import java.net.Socket;

public class SocketHandler {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    /**
     * Sends the card ID to the server and passes the response to the shared ScreenDriver instance.
     */
    public static String sendCardInfoToCardReader(String cardId) {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send card in the correct format so CardReader can process it
            String cardCommand = "CRreader:" + cardId;
            out.println(cardCommand);  // send to card reader via shared server

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }

            String finalMessage = response.toString().trim();
            ScreenDriver.getInstance().readExternalMessage("scd" + finalMessage);
            return finalMessage;

        } catch (IOException e) {
            String error = "scdError: " + e.getMessage();
            ScreenDriver.getInstance().readExternalMessage(error);
            return error;
        }
    }




}
