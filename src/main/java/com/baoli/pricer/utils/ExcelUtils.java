package com.baoli.pricer.utils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;

public class ExcelUtils {
    private static final DataFormatter FORMATTER = new DataFormatter();

    /**
     * 把单元格当前在 Excel 界面上显示的“数字”提取出来。
     * 支持 NUMERIC、FORMULA、STRING，完全不触发外部引用重算。
     *
     * @param cell POI 单元格对象
     * @return 如果能拿到数字则返回 Double，否则返回 null
     */
    public static Double getDisplayedNumber(Cell cell) {
        if (cell == null) return null;

        // 1) 纯数值
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }

        // 2) 公式：只看“缓存结果类型”
        if (cell.getCellType() == CellType.FORMULA) {
            CellType cached = cell.getCachedFormulaResultType();
            if (cached == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else if (cached == CellType.STRING) {
                String s = cell.getStringCellValue().trim();
                try {
                    return Double.parseDouble(s.replaceAll(",", ""));
                } catch (NumberFormatException ignored) {
                }
            }
            return null;
        }

        // 3) 文本：用 DataFormatter 拿“显示文本”
        String text = FORMATTER.formatCellValue(cell).trim();
        if (text.isEmpty()) return null;

        // 尝试把“显示文本”转成 Double
        try {
            return Double.parseDouble(text.replaceAll(",", ""));
        } catch (NumberFormatException e) {
            // 比如“—”或“NA”，则识别不了就返回 null
            return null;
        }
    }
}
