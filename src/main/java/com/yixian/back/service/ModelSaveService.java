package com.yixian.back.service;

import weka.classifiers.Classifier;
import weka.core.SerializationHelper;

/**
 * @author yi xian
 * @description 模型保存
 * @date 2023/10/12 10:09
 */
public class ModelSaveService {
    public void saveModel(Classifier classifier, String modelFileName) throws Exception {
        SerializationHelper.write(modelFileName, classifier);
    }
}
