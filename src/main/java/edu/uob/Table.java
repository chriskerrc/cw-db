package edu.uob;
import java.io.*;

public class Table {

    String fileName = "people.tab";
    String filePath = "databases" + File.separator + fileName;

    public boolean doesFileExist() {
        File fileToOpen = new File(filePath);
        return fileToOpen.exists();
    }

    public void readFileToConsole() throws IOException {
        File fileToOpen = new File(filePath);
        FileReader reader = new FileReader(fileToOpen);
        BufferedReader buffReader = new BufferedReader(reader);
        String line;
        while ((line = buffReader.readLine()) != null) {
            System.out.println(line);
        }
        buffReader.close();
    }

    //write table file
}
