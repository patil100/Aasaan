package Read_Write_Files;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;

public class ReadFromXlsxFile {

    static XSSFWorkbook wb;

    public void ReadXlsx(String path, int indexOfSheet) throws Exception {

        try {

            File src = new File(path);
            FileInputStream fileInputStream = new FileInputStream(src);
            wb = new XSSFWorkbook(fileInputStream);

        } catch (Exception e) {
            System.out.println("File not found \n\n" + e.getMessage());
        }

    }

    public String ReadCellXlsx(int indexOfSheet, int rowIndex, int columnIndex) throws Exception {

        XSSFSheet sh = wb.getSheetAt(indexOfSheet);
        return sh.getRow(rowIndex).getCell(columnIndex).getStringCellValue();

    }

    public void ViesCompleteSheet(int indexOfSheet) throws Exception {

        try {

            XSSFSheet sh = wb.getSheetAt(indexOfSheet);
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

    public String ReadCellFormulaXlsx(int indexOfSheet, int rowIndex, int columnIndex) throws Exception {

        XSSFSheet sh = wb.getSheetAt(indexOfSheet);
        return sh.getRow(rowIndex).getCell(columnIndex).getCellFormula();
    }

    public CellType ReadCellTypeXlsx(int indexOfSheet, int rowIndex, int columnIndex) throws Exception {

        XSSFSheet sh = wb.getSheetAt(indexOfSheet);
        return sh.getRow(rowIndex).getCell(columnIndex).getCellType();
    }

    public XSSFHyperlink ReadCellHyperlinkXlsx(int indexOfSheet, int rowIndex, int columnIndex) throws Exception {

        XSSFSheet sh = wb.getSheetAt(indexOfSheet);
        return sh.getRow(rowIndex).getCell(columnIndex).getHyperlink();
    }

    public double ReadCellNumericValueXlsx(int indexOfSheet, int rowIndex, int columnIndex) throws Exception {

        XSSFSheet sh = wb.getSheetAt(indexOfSheet);
        return sh.getRow(rowIndex).getCell(columnIndex).getNumericCellValue();
    }

    public void closeWorkBook() throws Exception {

        wb.close();

    }
}
