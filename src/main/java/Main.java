// --- Main.java ---
import Card.*;
import Control.VotingControl;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        CardReader cardReader = new CardReader();
        CardReaderDriver driver = new CardReaderDriver(cardReader);
        CardHolder holder = new CardHolder(driver);
        VotingControl votingControl = new VotingControl(holder);

        new Thread(() -> runCardReaderServer(votingControl)).start();

        System.out.println("System booted. Waiting for card input...");
    }

    public static void runCardReaderServer(VotingControl votingControl) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Card Reader Server started at port 12345");
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String cardData = in.readLine();
                    votingControl.notifyCardInserted(cardData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
