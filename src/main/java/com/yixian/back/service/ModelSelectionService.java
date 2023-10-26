package com.yixian.back.service;

import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.util.List;
import java.util.Map;

/**
 * @author yi xian
 * @description 模型选择类
 * @date 2023/10/11 16:23
 */
@Service
public class ModelSelectionService {

    public Classifier handleData(Map<String, Object> data, Instances dataset){
        // 从data中获取componentData
        List<Map<String, Object>> componentDataList = (List<Map<String, Object>>) data.get("componentData");
        Classifier classifier = null;
        // 遍历componentDataList
        for (Map<String, Object> componentData : componentDataList) {
            // 获取propValue的值
            String propValue = (String) componentData.get("propValue");
            // 根据不同的propValue值执行不同的操作
            if ("随机森林".equals(propValue)) {
                classifier = selectRandomForestModel();
            } else if ("决策树".equals(propValue)) {
                classifier = selectDecisionTreeModel();
            } else if ("朴素贝叶斯".equals(propValue)) {
                classifier = selectNaiveBayesModel();
            } else if ("SVM".equals(propValue)) {
                classifier = selectSVMModel();
            } else if ("KNN".equals(propValue)) {
                classifier = selectKNNModel();
            } else {
                // 处理其他未知的propValue值
            }
        }
        return classifier;
    }

    /**
     *  随机森林算法
     */
    public Classifier selectRandomForestModel() {
        // 创建分类器，这里使用RandomForest算法
        return new RandomForest();
    }

    private Classifier selectNaiveBayesModel() {
        return new NaiveBayes();
    }

    private Classifier selectDecisionTreeModel() {
        return new J48();
    }

    public Classifier selectSVMModel() {
        return new SMO();
    }

    public Classifier selectKNNModel() {
        return new IBk();
    }
}
