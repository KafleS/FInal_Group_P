package Hardwares.SDCards;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;

public class SDCard {
    private final String filepath;
    private Operation operation;
    private boolean status = false;

    public SDCard(int slotNumber, Operation operation) {
        System.out.println("[DEBUG] Slot number selected: " + slotNumber);
        this.filepath = switch (slotNumber) {
            case 0 -> "src/main/resources/ballot.txt";
            case 1 -> "src/main/resources/voter1.txt";
            case 2 -> "src/main/resources/voter2.txt";
            default -> throw new IllegalStateException("Unsupported slot number. Use 0, 1, or 2.");
        };
        this.operation = operation;
    }

    //Reads the file if exits else create empty list
    public List<String> read() throws IOException {
        if (operation == Operation.read) {
            Path file = Paths.get(filepath);
            if (!Files.exists(file)) {
                // Return empty list if file doesn't exist
                return Collections.emptyList();
            }
            return Files.readAllLines(file, StandardCharsets.UTF_8);
        } else if (operation == null) {
            throw new IOException("No SD card in slot");
        } else {
            throw new IOException("Unable to read from file as operation is not read");
        }
    }

    public void write(String text) throws IOException {

        if (operation == Operation.write) {
            Path file = Paths.get(filepath);

            // Ensure file and parent directory exist
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (!Files.exists(file)) {
                Files.createFile(file);
            }

            List<String> txt = Files.readAllLines(file, StandardCharsets.UTF_8);
            txt.add(text);
            Files.write(file, txt, StandardCharsets.UTF_8);
        } else if (operation == null) {
            throw new IOException("No SD card in slot");
        } else {
            throw new IOException("Unable to write to file as operation is not write");
        }
    }


    public void overwrite(String text) throws IOException {
        if (operation == Operation.overwrite) {
            Path file = Paths.get(filepath);

            // Ensure file and parent directory exist
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (!Files.exists(file)) {
                Files.createFile(file);
            }

            Files.write(file, Collections.singleton(text), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } else if (operation == null) {
            throw new IOException("No SD card in slot");
        } else {
            throw new IOException("Unable to overwrite file as operation is not overwrite");
        }
    }

    public void eject() {
        operation = null;
    }

    public boolean failure() {
        return status;
    }

    public enum Operation {
        read, write, overwrite
    }

    public void setFailure(boolean status) {
        this.status = status;
    }
}

