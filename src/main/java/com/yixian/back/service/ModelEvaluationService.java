package com.yixian.back.service;

import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;

/**
 * @author yi xian
 * @description 模型评估类
 * @date 2023/10/11 16:32
 */
public class ModelEvaluationService {

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
