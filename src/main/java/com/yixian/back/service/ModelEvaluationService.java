package com.yixian.back.service;

import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yi xian
 * @description 模型评估类
 * @date 2023/10/11 16:32
 */
@Service
public class ModelEvaluationService {

    public Map<String, Double> handleData(Map<String, Object> data, Classifier classifier, Instances dataset) throws Exception {
        Evaluation evaluation = evaluateModel(classifier,dataset);
        // 创建一个包含评估数据的字典
        Map<String, Double> result = new HashMap<>();
        // 从data中获取componentData
        List<Map<String, Object>> componentDataList = (List<Map<String, Object>>) data.get("componentData");
        // 遍历componentDataList
        for (Map<String, Object> componentData : componentDataList) {
            // 获取propValue的值
            String propValue = (String) componentData.get("propValue");
            // 根据不同的propValue值执行不同的操作
            if ("精确率".equals(propValue)) {
                result.put("精确率", evaluation.precision(0));
                System.out.println("精确率："+result.get("精确率"));
            } else if("召回率".equals(propValue)) {
                result.put("召回率", evaluation.recall(0));
                System.out.println("召回率："+result.get("召回率"));
            }else if("F1值".equals(propValue)){
                result.put("F1值", evaluation.fMeasure(0));
                System.out.println("F1值："+result.get("F1值"));
            }


        }
        return result;
    }

    /**
     * 传入评估模型和数据集 采用10折交叉验证
     * 数据集将分为10部分，每次迭代使用9部分用于训练，1部分用于测试。
     */
    public Evaluation evaluateModel(Classifier classifier, Instances dataset) throws Exception {
        Evaluation evaluation = new Evaluation(dataset);
        evaluation.crossValidateModel(classifier, dataset, 10, new java.util.Random(1));
        return evaluation;
    }

    /**
     * 打印模型信息
     */
    public void printEvaluationResults(Evaluation evaluation) {
        System.out.println("Evaluation Results:");
        System.out.println(evaluation.toSummaryString());
        System.out.println("Recall: " + evaluation.recall(0));
        System.out.println("F1 Score: " + evaluation.fMeasure(0));
        System.out.println("Precision: " + evaluation.precision(0));
    }
}
