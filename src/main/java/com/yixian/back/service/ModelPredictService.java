package com.yixian.back.service;

import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * @author yi xian
 * @description 模型预测类
 * @date 2023/10/11 17:07
 */
public class ModelPredictService {
    public double predictWithModel(Classifier classifier, Instances dataset, double[] values) throws Exception {
        Instances newInstance = new Instances(dataset, 0);
        newInstance.add(new weka.core.DenseInstance(1.0, values));
        return classifier.classifyInstance(newInstance.firstInstance());
    }
}
