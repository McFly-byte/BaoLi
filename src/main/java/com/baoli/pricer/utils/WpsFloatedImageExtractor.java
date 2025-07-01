package com.baoli.pricer.utils;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.XML;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class WpsFloatedImageExtractor {

    public static void main(String[] args) throws Exception {
//        byte[] data = Files.readAllBytes(new File("D:\\_Temp_MingchengL\\IDEA_proj\\pricer\\test2.xlsx").toPath());
//        Map<String, XSSFPictureData> pics = getPictures(data);
//        for (Map.Entry<String, XSSFPictureData> e : pics.entrySet()) {
//            String id = e.getKey();
//            XSSFPictureData pic = e.getValue();
//            String ext = pic.suggestFileExtension();
//            File out = new File(id + "." + ext);
//            Files.write(out.toPath(), pic.getData());
//            System.out.println("Extracted: " + id + " -> " + out.getName());
//        }
    }

    public static Map<String, XSSFPictureData> getPictures(byte[] data) throws Exception {
        Map<String, String> mapConfig = processZipEntries(new ByteArrayInputStream(data));
        Map<String, XSSFPictureData> mapPictures = processPictures(new ByteArrayInputStream(data), mapConfig);

        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(data))) {
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                XSSFSheet sheet = wb.getSheetAt(i);
                Map<String, XSSFPictureData> floats = getFloatingPictures(sheet);
                mapPictures.putAll(floats);
            }
        }

        return mapPictures;
    }

    private static Map<String, String> processZipEntries(InputStream is) throws IOException {
        Map<String, String> map = new HashMap<>();
        try (ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                if ("xl/cellImages.xml".equals(name)) {
                    processCellImages(zis, map);
                } else if ("xl/_rels/cellImages.xml.rels".equals(name)) {
                    processRels(zis, map);
                }
            }
        }
        return map;
    }

    private static void processCellImages(InputStream is, Map<String,String> map) throws IOException {
        String xml = IOUtils.toString(is, StandardCharsets.UTF_8);
        JSONObject jo = XML.toJSONObject(xml);
        JSONObject cellImages = jo.getJSONObject("etc:cellImages");
        if (cellImages == null) return;
        JSONArray arr = cellImages.getJSONArray("etc:cellImage");
        if (arr == null) {
            Object single = cellImages.get("etc:cellImage");
            if (single instanceof JSONObject) {
                arr = new JSONArray();
                arr.add(single);
            }
        }
        if (arr != null) {
            for (Object o : arr) {
                JSONObject img = (JSONObject) o;
                String id = img.getJSONObject("xdr:pic")
                        .getJSONObject("xdr:nvPicPr")
                        .getJSONObject("xdr:cNvPr")
                        .getStr("name");
                String embed = img.getJSONObject("xdr:pic")
                        .getJSONObject("xdr:blipFill")
                        .getJSONObject("a:blip")
                        .getStr("r:embed");
                map.put(embed, id);
            }
        }
    }

    private static void processRels(InputStream is, Map<String,String> map) throws IOException {
        String xml = IOUtils.toString(is, StandardCharsets.UTF_8);
        JSONObject jo = XML.toJSONObject(xml);
        JSONArray arr = jo.getJSONObject("Relationships").getJSONArray("Relationship");
        if (arr == null) {
            Object single = jo.getJSONObject("Relationships").get("Relationship");
            if (single instanceof JSONObject) {
                arr = new JSONArray();
                arr.add(single);
            }
        }
        if (arr != null) {
            for (Object o : arr) {
                JSONObject rela = (JSONObject) o;
                String id = rela.getStr("Id");
                String target = rela.getStr("Target"); // e.g. media/image1.png
                if (map.containsKey(id)) {
                    String name = map.get(id);
                    map.put("/xl/" + target, name);
                }
            }
        }
    }

    private static Map<String, XSSFPictureData> processPictures(InputStream is, Map<String,String> mapConfig) throws Exception {
        Map<String, XSSFPictureData> map = new HashMap<>();
        try (XSSFWorkbook wb = (XSSFWorkbook) WorkbookFactory.create(is)) {
            for (XSSFPictureData pic : wb.getAllPictures()) {
                String uri = pic.getPackagePart().getPartName().getName();
                if (mapConfig.containsKey(uri)) {
                    String name = mapConfig.get(uri);
                    map.put(name, pic);
                }
            }
        }
        return map;
    }

    private static Map<String, XSSFPictureData> getFloatingPictures(XSSFSheet sheet) {
        Map<String, XSSFPictureData> map = new HashMap<>();
        if (sheet.getDrawingPatriarch() != null) {
            sheet.getDrawingPatriarch().getShapes().forEach(sh -> {
                if (sh instanceof org.apache.poi.xssf.usermodel.XSSFPicture pic) {
                    String key = pic.getClientAnchor().getRow1() + "-" + pic.getClientAnchor().getCol1();
                    map.put(key, pic.getPictureData());
                }
            });
        }
        return map;
    }
}

