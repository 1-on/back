package com.yixian.back.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;


/**
 * @author yi xian
 * @description
 * @date 2023/10/11 16:03
 */
@Service
public class FileProcessing {
    public Instances loadCSVFile(MultipartFile file) throws IOException {
        // 临时保存上传的文件
        File tempFile = File.createTempFile("uploaded_csv_", ".csv");
        try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
            fileOutputStream.write(file.getBytes());
        }
        // 使用Weka的CSVLoader加载CSV文件
        CSVLoader loader = new CSVLoader();
        loader.setSource(tempFile);
        // 加载CSV数据
        Instances dataset = loader.getDataSet();
        // 删除临时文件
        tempFile.delete();
        return dataset;
    }

    public Instances loadExcelFileAsCSV(MultipartFile file) throws IOException {
        File tempExcelFile = File.createTempFile("uploaded_excel_", ".xlsx"); // 或者使用.xls扩展名，根据实际情况选择
        File tempCSVFile = File.createTempFile("uploaded_csv_", ".csv");

        try (FileOutputStream fileOutputStream = new FileOutputStream(tempExcelFile)) {
            fileOutputStream.write(file.getBytes());
        }

        // 根据文件扩展名选择合适的Workbook
        Workbook workbook;

        if (file.getOriginalFilename().endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(new FileInputStream(tempExcelFile));
        } else if (file.getOriginalFilename().endsWith(".xls")) {
            workbook = new HSSFWorkbook(new FileInputStream(tempExcelFile));
        } else {
            throw new IllegalArgumentException("不支持的文件格式");
        }

        // 从Excel文件中读取数据并写入CSV文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempCSVFile))) {
            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
            while (sheetIterator.hasNext()) {
                Sheet sheet = sheetIterator.next();
                Iterator<Row> rowIterator = sheet.iterator();
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    Iterator<Cell> cellIterator = row.iterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        String cellValue = cell.toString();
                        writer.write(cellValue);
                        if (cellIterator.hasNext()) {
                            writer.write(",");
                        }
                    }
                    writer.newLine();
                }
            }
        }

        // 使用Weka的CSVLoader加载CSV文件
        CSVLoader loader = new CSVLoader();
        loader.setSource(tempCSVFile);

        // 加载CSV数据
        Instances dataset = loader.getDataSet();

        // 删除临时文件
        tempExcelFile.delete();
        tempCSVFile.delete();

        return dataset;
    }

    public void viewCSVFile(MultipartFile file) {
        try {
            // 使用BufferedReader读取上传的CSV文件
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                // 分割CSV行
                String[] columns = line.split(",");
                // 在这里处理每一行的数据
                String column1 = columns[0]; // 第一列数据
                String column2 = columns[1]; // 第二列数据

                // 在这里可以对读取到的数据进行处理，例如打印到控制台
                System.out.println("Column 1: " + column1);
                System.out.println("Column 2: " + column2);

                // 或者将数据存储到其他数据结构中，或执行其他逻辑操作
            }

            // 关闭BufferedReader
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            // 处理异常
        }
    }
}
