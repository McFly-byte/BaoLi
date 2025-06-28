package com.baoli.pricer.utils;

import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

// 1. 自定义 CellWriteHandler：对“合计”行的总价列染黄
public class SummaryCellStyleHandler implements CellWriteHandler {

    private final int summaryRowIndex;
    private final int totalPriceColIndex;

    /**
     * @param summaryRowIndex  从 0 开始的行号（包含表头），合计行会写在这里
     * @param totalPriceColIndex 从 0 开始的“总价”列下标
     */
    public SummaryCellStyleHandler(int summaryRowIndex, int totalPriceColIndex) {
        this.summaryRowIndex = summaryRowIndex;
        this.totalPriceColIndex = totalPriceColIndex;
    }


    public void afterCellDispose(WriteSheetHolder sheetHolder,
                                 WriteTableHolder tableHolder,
                                 CellWriteHandlerContext context) {
        // 判断当前是合计行、总价列
        if (context.getRowIndex() == summaryRowIndex
                && context.getColumnIndex() == totalPriceColIndex) {
            // 设置黄色填充
            Cell cell = context.getCell();
            CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
            style.cloneStyleFrom(cell.getCellStyle());
            style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cell.setCellStyle(style);
        }
    }
}

