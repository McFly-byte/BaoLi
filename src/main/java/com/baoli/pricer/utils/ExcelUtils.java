package com.baoli.pricer.utils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;

public class ExcelUtils {
    public static final DataFormatter FORMATTER = new DataFormatter();

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


    /**
     * 获取单元格当前在 Excel/WPS 界面上展示的“文字”内容。
     * 对于 FORMULA：**只**读“缓存结果”，绝不触发外部引用或者使用 DataFormatter 去格式化公式本身。
     * 对于 NUMERIC、STRING、其他：按原逻辑处理。
     *
     * @param cell POI 单元格对象
     * @return WPS/Excel 界面上看到的原始文本（包含多行换行符、单位、中文等），若为空或仅有空白，返回 null
     */
    public static String getDisplayedText(Cell cell) {
        if (cell == null) {
            return null;
        }

        // 1) 公式单元格：只读缓存结果类型
        if (cell.getCellType() == CellType.FORMULA) {
            CellType cached = cell.getCachedFormulaResultType();
            switch (cached) {
                case STRING:
                    String s = cell.getStringCellValue();
                    return (s == null || s.trim().isEmpty()) ? null : s.trim();
                case NUMERIC:
                    // 数字／日期要保留界面格式（含千分位、小数、日期格式等）
                    String numText = FORMATTER.formatCellValue(cell);
                    return numText.trim().isEmpty() ? null : numText.trim();
                case BLANK:
                    return null;
                default:
                    // 遇到 ERROR、BOOLEAN 等也走 formatter，再严格 trim
                    String other = FORMATTER.formatCellValue(cell);
                    return other.trim().isEmpty() ? null : other.trim();
            }
        }

        // 2) 纯数值或日期：用 DataFormatter 保留格式
        if (cell.getCellType() == CellType.NUMERIC) {
            String text = FORMATTER.formatCellValue(cell);
            return text.trim().isEmpty() ? null : text.trim();
        }

        // 3) 纯文本
        if (cell.getCellType() == CellType.STRING) {
            String text = cell.getStringCellValue();
            return (text == null || text.trim().isEmpty()) ? null : text.trim();
        }

        // 4) 其他类型（BOOLEAN、ERROR）：也格式化一次
        String text = FORMATTER.formatCellValue(cell);
        return text.trim().isEmpty() ? null : text.trim();
    }


}
