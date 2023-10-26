package com.yixian.back.controller;

import com.yixian.back.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.Standardize;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author yi xian
 * @description
 * @date 2023/10/11 15:53
 */

@RestController
@RequestMapping("/api")
public class CSVUploadController {

    @Autowired
    private FileProcessing fileProcessing;

    @Autowired
    private FeatureEngineer featureEngineer;

    @Autowired
    private ModelSelectionService modelSelectionService;

    @Autowired
    private ModelEvaluationService modelEvaluationService;

    @Autowired
    private PreProcessing preProcessing;
    Instances dataset;

    Classifier classifier;

    @PostMapping("/upload2-csv")
    public String uploadCSV2(@RequestParam("file") MultipartFile file) throws Exception {

        Instances dataset = fileProcessing.loadCSVFile(file);
        // 设置类别属性
        dataset.setClassIndex(dataset.numAttributes() - 1);

        // 数据离散化
        Discretize discretizeFilter = new Discretize();
        discretizeFilter.setInputFormat(dataset);
        Instances discretizedData = Filter.useFilter(dataset, discretizeFilter);
        System.out.println(discretizedData);
        // 模型选择
        Classifier classifier = new ModelSelectionService().selectRandomForestModel();
        // 模型训练
        System.out.println("开始训练");
        classifier.buildClassifier(dataset);
        System.out.println("训练结束");
        // 评估模型
        System.out.println("开始评估");
        Evaluation evaluation = new ModelEvaluationService().evaluateModel(classifier, dataset);
        new ModelEvaluationService().printEvaluationResults(evaluation);

        System.out.println("评估完成");
        // 模型预测
        double[] inputValues = new double[]{5.66998, 0, 18.1, 1, 0.631, 6.683, 96.8, 1.3567, 24, 666, 20.2, 375.33, 3.73};
        double prediction = new ModelPredictService().predictWithModel(classifier, dataset, inputValues);

        System.out.println("预测完成");
        System.out.println("Predicted class index: " + prediction);
        System.out.println("Predicted class: " + dataset.classAttribute().value((int) prediction));
        // 模型保存
//        String modelFileName = "your_model.model"; // 选择模型文件名
//        new ModelSaveService().saveModel(classifier,modelFileName);

        return null;
    }

    @PostMapping("/upload-csv")
    public String uploadCSV(@RequestParam("file") MultipartFile file) throws Exception {

//        fileProcessing.viewCSVFile(file);

        dataset = fileProcessing.loadCSVFile(file);
        dataset.setClassIndex(dataset.numAttributes() - 1);
        return null;
    }

    @PostMapping("/upload-excel")
    public String uploadExcel(@RequestParam("file") MultipartFile file) throws Exception {
        dataset = fileProcessing.loadExcelFileAsCSV(file);
        System.out.println(dataset);
        dataset.setClassIndex(dataset.numAttributes() - 1);
        return null;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleUpload(@RequestBody Map<String, Object> requestData) throws Exception {
        try {
            // 打印接收到的数据到控制台
            System.out.println("Received Data:");
            System.out.println(requestData);

            // 处理componentData
            List<Map<String, Object>> componentData = (List<Map<String, Object>>) requestData.get("componentData");
            processComponentData(componentData);

            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("上传失败");
        }


    }

    private void processComponentData(List<Map<String, Object>> componentData) throws Exception {
        for (Map<String, Object> component : componentData) {
            processComponent(component);
        }
    }

    private void processComponent(Map<String, Object> component) throws Exception {
        for (Map.Entry<String, Object> entry : component.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                // 如果值是一个Map，递归处理它
                System.out.println("Key: " + key);
                processComponent((Map<String, Object>) value);
            } else {
                if (value.equals("随机森林")) {
                    // 设置类别属性
                    dataset.setClassIndex(dataset.numAttributes() - 1);
                    // 模型选择
                    Classifier classifier = new ModelSelectionService().selectRandomForestModel();
                    // 模型训练
                    System.out.println("开始训练");
                    classifier.buildClassifier(dataset);
                    System.out.println("训练结束");
                }
                // 处理具体的属性
                System.out.println("Key: " + key + ", Value: " + value);
            }
        }
    }


    @PostMapping("/upload{type}")
    public ResponseEntity<String> uploadDataByType(@PathVariable String type, @RequestBody Map<String, Object> data) throws Exception {
        // 在这里，根据传入的type参数，可以执行不同的处理逻辑
        // 比如，type=1 表示处理 type 为 1 的组件数据
        // 你可以根据需要执行数据预处理、特征工程、模型训练等操作
        Map<String, Double> result = null;
        if (Objects.equals(type, "2")) {
            dataset = preProcessing.MissingValueHandle(dataset);
        } else if (Objects.equals(type, "3")) {
            dataset = featureEngineer.handleData(data, dataset);
        } else if (Objects.equals(type, "4")) {
            classifier = modelSelectionService.handleData(data, dataset);
        } else if (Objects.equals(type, "5")) {
            result = modelEvaluationService.handleData(data, classifier, dataset);
        }

        // 输出接收到的数据
        System.out.println("Received data for type " + type + ":");
        System.out.println(data);
        String responseMessage = "Success"; // 默认成功消息

        // 根据不同的type值，返回不同的消息
        if (Objects.equals(type, "1")) {
            responseMessage = "数据上传完成";
        }else if (Objects.equals(type, "2")) {
            responseMessage = "数据预处理完成";
        } else if (Objects.equals(type, "3")) {
            responseMessage = "特征工程完成";
        } else if (Objects.equals(type, "4")) {
            responseMessage = "模型训练完成";
        } else if (Objects.equals(type, "5")) {
            if (result != null) {
                responseMessage = result.toString();
            }
        }

        return ResponseEntity.ok(responseMessage);
    }
}
