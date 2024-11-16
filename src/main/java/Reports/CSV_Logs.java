package Reports;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


/*
void CsvLogs(List<String[]> data)  //Writes the data and stores in csv file
 */

public class CSV_Logs {

    public static void CsvLogs(List<String[]> data) throws IOException {

        File file1 = new File(System.getProperty("user.dir") + "\\CSV_Report");
        File file = new File(System.getProperty("user.dir") + "\\CSV_Report\\CSV_Report.csv");

        try {

            FileWriter outputfile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputfile);
            writer.writeAll(data);
            writer.close();

        } catch (Exception e) {
            System.out.println("Error occurred while writing logs to csv file \n\n"+e.getMessage());
        }
    }
}
