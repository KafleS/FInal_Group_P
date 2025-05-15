package Client;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class SocketHandler {
    private static SocketHandler instance;

    protected final Socket socket;
    protected ObjectInputStream input;
    protected ObjectOutputStream output;
    protected PrintWriter writer;

    // Private constructor
    private SocketHandler(Socket socket) {
        this.socket = socket;
        try {
            this.output = new ObjectOutputStream(socket.getOutputStream());
            this.input = new ObjectInputStream(socket.getInputStream());
            this.writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Unable to get input/output streams.");
            close();
        }
    }

    public static void initialize(Socket socket) {
        if (instance == null) {
            instance = new SocketHandler(socket);
        }
    }

    public static SocketHandler getInstance() {
        return instance;
    }

    public ObjectInputStream getInputStream() {
        return input;
    }

    public ObjectOutputStream getOutputStream() {
        return output;
    }

    public Socket getSocket() {
        return socket;
    }




    public void close() {
        try {
            List<Closeable> objects = Arrays.asList(input, output, socket);
            for (Closeable object : objects) {
                if (object != null) object.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing all objects...");
        }
    }
}
