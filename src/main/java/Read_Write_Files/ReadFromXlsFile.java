package Read_Write_Files;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import java.io.File;
import java.io.FileInputStream;


/*
void ReadXls(String path, int indexOfSheet)  //Creates new xls file

String ReadCellXls(int indexOfSheet, int rowIndex, int columnIndex)  //Returns data from mentioned row,column number

void ViesCompleteSheet(int indexOfSheet)  //Outputs the contents of sheet to console

CellType ReadCellTypeXls(int indexOfSheet, int rowIndex, int columnIndex)  //Reads the hyperlink

void closeWorkBook()  //Closes the workbook created
 */
public class ReadFromXlsFile {

    static HSSFWorkbook wb;
    public void ReadXls(String path){

        try {

            File src = new File(path);
            FileInputStream fileInputStream = new FileInputStream(src);
            wb = new HSSFWorkbook(fileInputStream);

        } catch (Exception e) {
            System.out.println("File not found \n\n" + e.getMessage());
        }

    }

    public String ReadCellXls(int indexOfSheet, int rowIndex, int columnIndex) throws Exception {

        HSSFSheet sh = wb.getSheetAt(indexOfSheet);
        return sh.getRow(rowIndex).getCell(columnIndex).getStringCellValue();

    }

    public void ViesCompleteSheet(int indexOfSheet){

        try {

            HSSFSheet sh = wb.getSheetAt(indexOfSheet);
            int rowCount = sh.getLastRowNum();
            int colCount = sh.getRow(0).getLastCellNum();

            for (int i = 0; i < rowCount; i++) {

                for (int x = 0; x < colCount; x++) {

                    System.out.println(sh.getRow(i).getCell(x).getStringCellValue() + "  ");
                }
                System.out.println("\n");
            }

        } catch (Exception e) {
            System.out.println("Value not found \n\n" + e.getMessage());
        }
    }

    public String ReadCellFormulaXls(int indexOfSheet, int rowIndex, int columnIndex){

        HSSFSheet sh = wb.getSheetAt(indexOfSheet);
        return sh.getRow(rowIndex).getCell(columnIndex).getCellFormula();

    }

    public CellType ReadCellTypeXls(int indexOfSheet, int rowIndex, int columnIndex) throws Exception {

        HSSFSheet sh = wb.getSheetAt(indexOfSheet);
        return sh.getRow(rowIndex).getCell(columnIndex).getCellType();

    }

    public HSSFHyperlink ReadCellHyperlinkXls(int indexOfSheet, int rowIndex, int columnIndex) throws Exception {

        HSSFSheet sh = wb.getSheetAt(indexOfSheet);
        return sh.getRow(rowIndex).getCell(columnIndex).getHyperlink();

    }

    public double ReadCellNumericValueXls(int indexOfSheet, int rowIndex, int columnIndex) throws Exception {

        HSSFSheet sh = wb.getSheetAt(indexOfSheet);
        return sh.getRow(rowIndex).getCell(columnIndex).getNumericCellValue();

    }

    public void closeWorkBook() throws Exception {

        wb.close();

    }
}
