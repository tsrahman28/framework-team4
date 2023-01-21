package utility;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadFromExcel {

    FileInputStream excelFile;
    XSSFWorkbook workbook;
    XSSFSheet sheet;
    String filPath;
    String sheetName;
    public ReadFromExcel(String filPath, String sheetName){
        this.filPath = filPath;
        this.sheetName = sheetName;
    }

    public String getDataFromCell(int row, int column) {
        String data = null;
        try {
            excelFile = new FileInputStream(filPath);
            workbook = new XSSFWorkbook(excelFile);
            sheet = workbook.getSheet(sheetName);
            data = sheet.getRow(row).getCell(column).getStringCellValue();
            excelFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }
    public List<String> getEntireColumnData(int rowStart, int column) {
        List<String> data = new ArrayList<>();
        try {
            excelFile = new FileInputStream(filPath);
            workbook = new XSSFWorkbook(excelFile);
            sheet = workbook.getSheet(sheetName);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                data.add(sheet.getRow(i).getCell(column).getStringCellValue());
            }
            excelFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }
    public String getCellValueForGivenHeaderAndKey(String header, String key){
        String data = null;
        int i = 0;
        while(getDataFromCell(0, i) != null){
            if (getDataFromCell(0, i).equalsIgnoreCase(header)){
                for (int j = 0; j < getEntireColumnData(1, i).size(); j++) {
                    if (getEntireColumnData(1, i).get(j).equalsIgnoreCase(key)){
                        data = getEntireColumnData(1, i+1).get(j);
                    }

                }
            }
            break;
        }
        return data;
    }

    public static void main(String[] args) throws IOException {
        ReadFromExcel read = new ReadFromExcel("C:\\Users\\PNT\\IdeaProjects\\Nov2022AutomationFramework\\data\\titles.xlsx", "test data");
        read.getCellValueForGivenHeaderAndKey("key","home page title");
    }
}
