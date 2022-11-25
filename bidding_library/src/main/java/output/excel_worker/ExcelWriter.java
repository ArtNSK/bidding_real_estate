package output.excel_worker;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import output.excel_worker.exception.IllegalFileExtensionException;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Класс записывает документ Excel в файл
 * @author Shestakov Artem
 * */
@UtilityClass
@Slf4j
public class ExcelWriter {
    private static final String EXCEL_EXTENSION = ".xlsx";

    public void writeExcelFile(XSSFWorkbook workbook, String excelFilePath) {

        if (!excelFilePath.endsWith(EXCEL_EXTENSION)) {
            throw new IllegalFileExtensionException();
        }
        try (FileOutputStream out = new FileOutputStream(excelFilePath)) {
            workbook.write(out);
        } catch (IOException e) {
            log.warn("Ошибка {} при записи файла: {}",e.getClass().getName(), e.getMessage());
        }
    }
}
