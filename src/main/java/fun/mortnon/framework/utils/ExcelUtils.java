package fun.mortnon.framework.utils;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

/**
 * Excel 文件工具
 *
 * @author dev2007
 * @date 2024/2/29
 */
public class ExcelUtils {
    /**
     * 创建表格头文字
     *
     * @param sheet
     * @param headerNames
     */
    public static void createHeader(Sheet sheet, List<String> headerNames) {
        Row headerRow = sheet.createRow(0);
        for (int index = 0; index < headerNames.size(); index++) {
            sheet.autoSizeColumn(index);
            headerRow.createCell(index).setCellValue(headerNames.get(index));
        }
    }
}
