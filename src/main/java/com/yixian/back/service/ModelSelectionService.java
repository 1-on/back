package com.yixian.back.service;

import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;

/**
 * @author yi xian
 * @description 模型选择类
 * @date 2023/10/11 16:23
 */
public class ModelSelectionService {

    /**
     *  随机森林算法
     */
    public Classifier selectRandomForestModel() {
        // 创建分类器，这里使用RandomForest算法
        return new RandomForest();
    }
}
