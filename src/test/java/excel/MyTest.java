package excel;


import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class MyTest {

    public static void main(String[] args) throws InterruptedException, IOException {

        // List<ExcelCell> list = new ArrayList<ExcelCell>();
        // list.add(new ExcelCell(1, 2, "yaochenkun"));
        // list.add(new ExcelCell(3, 4, "yaochenkun1"));
        // list.add(new ExcelCell(5, 6, "yaochenkun2"));
        //
        // ExcelHelper.write("C:\\Users\\zlren\\Desktop\\xxx\\test.xlsx", list);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet inputSheet = workbook.createSheet("页名");

        {
            XSSFRow firstRow = inputSheet.createRow((short) 0);
            XSSFCell firstRowCell = firstRow.createCell((short) 0);
            firstRowCell.setCellValue("血常规");

            XSSFFont firstFont = workbook.createFont();
            firstFont.setColor(XSSFFont.COLOR_RED); // 红色
            firstFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD); // 加粗
            firstFont.setFontHeightInPoints((short) 14);

            XSSFCellStyle firstStyle = workbook.createCellStyle();
            firstStyle.setFont(firstFont);
            firstStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);

            firstRowCell.setCellStyle(firstStyle);

            inputSheet.addMergedRegion(new CellRangeAddress(
                    0, //first firstRow (0-based)
                    0, //last firstRow (0-based)
                    0, //first column (0-based)
                    4 //last column (0-based)
            ));
        }

        {
            XSSFRow secondRow = inputSheet.createRow((short) 1);

            XSSFFont boldFont = workbook.createFont();
            boldFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD); // 加粗

            XSSFCellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);
            boldStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);

            List<String> content = Arrays.asList("检查项目", "系统分类", "参考值", "医院", "检查结果");
            for (int i = 0; i < 5; i++) {
                XSSFCell cell = secondRow.createCell((short) i);
                cell.setCellStyle(boldStyle);
                cell.setCellValue(content.get(i));
            }

        }


        FileOutputStream out = new FileOutputStream(
                new File("C:\\Users\\zlren\\Desktop\\xxx\\test.xlsx"));
        workbook.write(out);
        out.close();
    }

}
