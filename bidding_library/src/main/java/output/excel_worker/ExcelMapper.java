package output.excel_worker;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Класс для преобразования списка входящих объектов в объект Excel
 * @author Shestakov Artem
 * */
@UtilityClass
@Slf4j
public class ExcelMapper {
    private static final String EXCEL_SHEET_NAME = "data";
    private static final String METHOD_NO_INFORMATION = "null";
    public static final int FIRST_RESPONSE = 0;

    /**
     * Метод класса преобразует список объектов в объект Excel
     * @param list Входящий список обхектов
     * @return {@link XSSFWorkbook} завернутый в {@link Optional}
     * */
    public <T> Optional<XSSFWorkbook> getExcelWorkbook(List<T> list) {
        if (list.isEmpty()) {
            return Optional.empty();
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(EXCEL_SHEET_NAME);
        int rowId = 0;
        List<Method> methods = getMethods(list.get(FIRST_RESPONSE));

        for (T objects : list) {
            if (objects == null) {
                continue;
            }
            XSSFRow row = sheet.createRow(rowId++);

            List<Object> data = getValuesForMethods(methods, objects);
            fillExcelRow(data, row);
        }
        return Optional.of(workbook);
    }

    /**
     * Метод заполняет строку в документе Excel
     * @param data Входящие данные для заполнения
     * @param row Заполняемая строка из документа
     * */
    private void fillExcelRow(List<Object> data, XSSFRow row) {
        AtomicInteger cellId = new AtomicInteger();
        try {
            data.forEach(element -> {
                Cell cell = row.createCell(cellId.getAndIncrement());
                cell.setCellValue((String) element);
            });
        } catch (Exception e) {
            log.warn("Ошибка {} при генерировании таблицы: {}", e.getClass().getName(), e.getMessage());
        }
    }

    /**
     * Метод заполняет значения для переданных методов (get для полей объекта)
     * @param methods Методы, для которых нужно заполнить значения
     * @param object Объект со значениями
     * @return Список значений
     * */
    private List<Object> getValuesForMethods(List<Method> methods, Object object) {
        List<Object> outputData = new ArrayList<>();
        for (Method method : methods) {
            try {
                String string = method.invoke(object).toString();
                outputData.add(string);
            } catch (Exception e) {
                log.info("Ошибка {} в подстановке метода {}: {}", e.getClass().getName(), method.getName(), e.getMessage());
                outputData.add(METHOD_NO_INFORMATION);
            }
        }
        return outputData;
    }

    /**
     * Метод возвращает возможные get полей для класса
     * @param clazz Входящий объект класса
     * @return Список get для полей
     * */
    public <T> List<Method> getMethods(T clazz) {
        return Arrays.stream(clazz.getClass().getMethods())
                .filter(method -> method.getName().startsWith("get") && !method.getName().equals("getClass"))
                .collect(Collectors.toList());
    }
}
