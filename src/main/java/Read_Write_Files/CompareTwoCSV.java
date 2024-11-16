package Read_Write_Files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/*
Boolean CompareTwoCsv(String path1,String path2)  //compares two csv files and returns the result
 */

public class CompareTwoCSV {
    public static Boolean CompareTwoCsv(String path1,String path2) throws IOException {
        List<String> File1 = Files.readAllLines(Paths.get(path1));
        List<String> File2 = Files.readAllLines(Paths.get(path2));
        return File1.equals(File2);
    }
    }
