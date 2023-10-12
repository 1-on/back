package com.yixian.back.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
}
