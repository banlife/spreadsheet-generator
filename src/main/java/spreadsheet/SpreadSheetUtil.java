package spreadsheet;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.UUID;

public class SpreadSheetUtil {
    public static <T> void sendSpreadSheetUtil(SpreadSheetParameter<T> parameter, HttpServletResponse response) throws IOException {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet(parameter.getSpreadSheetName());

        makeHeaderRow(parameter, sheet);
        makeCells(parameter, sheet);
        setResponseHeaders(response);
        sendResponse(response, workbook);
    }

    private static <T> void makeHeaderRow(SpreadSheetParameter<T> spreadSheetParameter, XSSFSheet sheet) {
        Row row;
        row = sheet.createRow(0);
        var cellTypes = spreadSheetParameter.getCellTypes();

        for (int cellIndex = 0; cellIndex < cellTypes.size(); cellIndex++) {
            var cell = row.createCell(cellIndex);
            cell.setCellValue(cellTypes.get(cellIndex));
        }
    }

    private static <T> void makeCells(SpreadSheetParameter<T> parameter, XSSFSheet sheet) {
        var rowIndex = 1;
        Row row;
        for (T target : parameter.getTargets()) {
            Cell cell;
            row = sheet.createRow(rowIndex++);

            for (int index = 0; index < parameter.getFunctions().size(); index++) {
                cell = row.createCell(index);

                var cellName = parameter.getCellTypes().get(index);
                if (parameter.isNumericCellName(cellName)) {
                    cell.setCellValue(Double.parseDouble(parameter.getFunctions().get(index).apply(target)));
                } else {
                    cell.setCellValue(parameter.getFunctions().get(index).apply(target));
                }
            }
        }
    }

    private static void setResponseHeaders(HttpServletResponse response) {
        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename=%s.xlsx".formatted(UUID.randomUUID().toString()));
    }

    private static void sendResponse(HttpServletResponse response, XSSFWorkbook workbook) throws IOException {
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
