package com.yixian.back.controller;

import com.yixian.back.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

    @PostMapping("/upload-csv")
    public String uploadCSV(@RequestParam("file") MultipartFile file) throws Exception {

        Instances dataset = fileProcessing.loadCSVFile(file);
        // 设置类别属性
        dataset.setClassIndex(dataset.numAttributes() - 1);
        // 模型选择
        Classifier classifier = new ModelSelectionService().selectRandomForestModel();
        // 模型训练
        classifier.buildClassifier(dataset);
        // 评估模型
        Evaluation evaluation = new ModelEvaluationService().evaluateModel(classifier, dataset);
        new ModelEvaluationService().printEvaluationResults(evaluation);
        // 模型预测
        double[] inputValues = new double[]{7.2, 3, 5.8, 1.6};
        double prediction = new ModelPredictService().predictWithModel(classifier,dataset,inputValues);

        System.out.println("Predicted class index: " + prediction);
        System.out.println("Predicted class: " + dataset.classAttribute().value((int) prediction));
        // 模型保存
        String modelFileName = "your_model.model"; // 选择模型文件名
        new ModelSaveService().saveModel(classifier,modelFileName);

        return null;
    }
}
