package com.baoli.pricer;

import com.baoli.pricer.dto.PageResult;
import com.baoli.pricer.pojo.Material;
import com.baoli.pricer.service.MaterialService;
import org.apache.poi.xssf.usermodel.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

@SpringBootTest
class PricerApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void parseXlsx() throws Exception {
        String inputFile = "D:\\_Temp_MingchengL\\IDEA_proj\\pricer\\test2.xlsx";
        File file = new File(inputFile);
        try (InputStream in = Files.newInputStream(file.toPath());
             XSSFWorkbook workbook = new XSSFWorkbook(in)) {

            XSSFSheet sheet = workbook.getSheetAt(0);
            System.out.println("Processing sheet: " + sheet.getSheetName());

            // 2. 获取浮动图形对象，用于检索内嵌的图片
            XSSFDrawing drawing = sheet.getDrawingPatriarch();
            if (drawing == null) {
                System.out.println("No drawing/pictures found.");
                return;
            }

            // 3. 遍历所有 Shape，过滤 XSSFPicture 类型
            List<XSSFShape> shapes = drawing.getShapes();
            int counter = 0;
            for (XSSFShape shape : shapes) {
                if (shape instanceof XSSFPicture pic) {
                    XSSFClientAnchor anc = pic.getPreferredSize();
                    int row = anc.getRow1();
                    int col = anc.getCol1();
                    byte[] data = pic.getPictureData().getData();
                    String ext = pic.getPictureData().suggestFileExtension();

                    System.out.printf("Image #%d at row=%d, col=%d, size=%d, ext=%s%n",
                            ++counter, row, col, data.length, ext);

                    // 4. 保存图片到本地目录
                    File outFile = new File("extracted_img_" + counter + "." + ext);
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        fos.write(data);
                    }
                }
            }
        }


    }

    @Autowired
    private MaterialService materialService;

    @Test
    void testPage() throws Exception {
        int page = 1;
        int size = 10;
        PageResult<Material> result = materialService.list(page, size);
        System.out.println(result);
    }
}
