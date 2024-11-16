package Read_Write_Files;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class WriteToCSV {
    static private File file;
    static private CSVWriter writer;
    static private List<String> temp;
    public static void initializeCsvWriter(String filePath) throws IOException {
        file = new File(filePath);
        FileWriter outputFile = new FileWriter(file, true);
        writer = new CSVWriter(outputFile,',',
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);
    }
    public static void deleteContentsOfCsv(String filePath) throws Exception {
        file = new File(filePath);
        ReadFromCSV r= new ReadFromCSV(filePath);
        String[] header_original= r.ReadLineNumber(0);
        if (file.exists() && file.isFile())
        {
            file.delete();
        }
        file.createNewFile();
        FileWriter outputFile = new FileWriter(file, true);
        // create CSVWriter object fileWriter object as parameter
        writer = new CSVWriter(outputFile,',',
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);
        writer.writeNext(header_original);
        writer.close();
    }
    public static void writeNextLineCsv(String [] data) throws IOException {
        writer.writeNext(data);
        writer.close();
    }
}