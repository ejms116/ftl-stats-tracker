package net.gausman.ftl;
import net.gausman.ftl.util.GausmanUtil;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class SaveFileReplayer {
    public static void main(String[] args) throws Exception {
        Path testFolder = Paths.get("saves\\test-holo-engi-a");
        Path targetFile = Paths.get("C:\\Users\\erikj\\Documents\\My Games\\FasterThanLight\\continue.sav");

        // Collect test files (sorted by name)
        List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(testFolder)) {
            for (Path file : stream) {
                files.add(file);
            }
        }
        files.sort(Comparator.comparingInt(file -> GausmanUtil.extractNumberAfterHyphen(file.toString())));
//        files.sort(Comparator.naturalOrder()); // sort alphabetically

        // Replay loop
        int index = 0;
        while (index < files.size()) {
            Path file = files.get(index);
            try {
                Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Copied " + file.getFileName());
                index++; // only move on if successful
            } catch (IOException e) {
                System.out.println("Could not copy " + file.getFileName() + ": " + e.getMessage());
                // don’t increment index → retry same file next cycle
            }

            Thread.sleep(100); // delay between cycles
        }

        System.out.println("Replay finished!");
    }
}

