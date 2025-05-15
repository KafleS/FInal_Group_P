package Client;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class Listener implements Runnable {
    private final Socket socket;
    private final Screen displayJavaFX;

    private ObjectInputStream input;
    private ObjectOutputStream output;

    public Listener(Socket socket, Screen displayJavaFX) {
        this.socket = socket;
        this.displayJavaFX = displayJavaFX;

        try {

            System.out.println("[Listener] Initializing streams...");
            this.output = new ObjectOutputStream(socket.getOutputStream());
            this.output.flush();
            this.input = new ObjectInputStream(socket.getInputStream());
            System.out.println("[Listener] Streams initialized.");
        } catch (IOException e) {
            System.err.println("[Listener] Failed to create streams: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendCardId(String cardId) {
        try {
            output.writeObject("CRreader:" + cardId);
            output.flush();
            System.out.println("[Listener] Sent card ID: " + cardId);
        } catch (IOException e) {
            System.err.println("[Listener] Failed to send card ID: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        System.out.println("[Listener] Listener thread started");

        Template currentTemplate = null;

        while (socket != null && socket.isConnected()) {
            try {
                // Step 1: Read object from server
                System.out.println("[Listener]  Waiting for object from server...");
                Object object = input.readObject();


                // Step 2: Template received — render it
                if (object instanceof Template temp) {
                    System.out.println("[Listener]  Received Template: " + temp.getTitle());
                    currentTemplate = temp;

                    // Show it in GUI
                    Platform.runLater(() -> displayJavaFX.receiveTemplate(temp));

                    // Wait for GUI interaction
                    while (!displayJavaFX.isReady()) {
                        Thread.sleep(200); // Small wait before checking again
                    }


                    currentTemplate = null; // Clear after ready
                }

                else if (object instanceof String tempstring) {
                    System.out.println("[Listener] Received string: " + tempstring);

                    if (tempstring.equalsIgnoreCase("isready")) {
                        boolean ready = displayJavaFX.isReady();
                        try {
                            output.writeObject(ready);
                            output.flush();
                            System.out.println("[Listener]  Responded isReady: " + ready);
                        } catch (IOException e) {
                            System.err.println("[Listener]  Failed to send isReady response: " + e.getMessage());
                        }
                    }


                    else if (tempstring.equalsIgnoreCase("resetToLogin")) {
                        System.out.println("[Listener]  Received command: resetToLogin");
                        Platform.runLater(() -> displayJavaFX.resetToCardInsertPage());
                    }


                    else if (tempstring.equalsIgnoreCase("gettemplate")) {
                        Template filled = displayJavaFX.returnTemplate();
                        try {
                            output.writeObject(filled);
                            output.flush();
                            System.out.println("[Listener]  Sent modified template to server.");
                        } catch (IOException e) {
                            System.err.println("[Listener]  Failed to send template: " + e.getMessage());
                        }
                    }
                    //new code
                    else {
                        if (tempstring.toLowerCase().contains("voting is not open")) {
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Voting Closed");
                                alert.setHeaderText(null);
                                alert.setContentText("Voting is not open. Please contact an official.");
                                alert.showAndWait();
                            });
                        } else {
                            System.out.println("[Listener]  Unhandled string: " + tempstring);
                        }
                    }
                }
                else {
                    System.out.println("[Listener] Unknown object received: " + object.getClass());
                }

            } catch (OptionalDataException ode) {
                System.err.println("[Listener]  OptionalDataException — mixed stream data?");
                ode.printStackTrace();
                break;

            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                System.err.println("[Listener]  Communication error.");
                e.printStackTrace();
                break;
            }
        }

        System.out.println("[Listener]  Socket disconnected.");
    }
}
