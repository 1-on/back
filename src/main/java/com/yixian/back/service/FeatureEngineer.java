package com.yixian.back.service;

import org.springframework.stereotype.Service;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.Standardize;

import java.util.List;
import java.util.Map;

/**
 * @author yi xian
 * @description 特征工程类
 * @date 2023/10/22 14:50
 */
@Service
public class FeatureEngineer {

    public Instances handleData(Map<String, Object> data,Instances dataset) throws Exception {
        // 从data中获取componentData
        List<Map<String, Object>> componentDataList = (List<Map<String, Object>>) data.get("componentData");
        Instances temp = null;
        // 遍历componentDataList
        for (Map<String, Object> componentData : componentDataList) {
            // 获取propValue的值
            String propValue = (String) componentData.get("propValue");
            // 根据不同的propValue值执行不同的操作
            if ("归一化".equals(propValue)) {
                // 执行归一化操作
                temp = performNormalization(dataset);
            } else if ("标准化".equals(propValue)) {
                // 执行标准化操作
                temp = performStandardization(dataset);
            } else if ("离散化".equals(propValue)) {
                // 执行离散化操作
                temp = performDiscretization(dataset);
            } else {
                // 处理其他未知的propValue值
            }
        }
        return temp;
    }

    // 数据归一化
    public Instances performNormalization(Instances data) throws Exception {
        Normalize normalizeFilter = new Normalize();
        normalizeFilter.setInputFormat(data);
        Instances normalizedData = Filter.useFilter(data, normalizeFilter);
        return normalizedData;
    }

    // 数据标准化
    public Instances performStandardization(Instances data) throws Exception {
        Standardize standardizeFilter = new Standardize();
        standardizeFilter.setInputFormat(data);
        Instances standardizedData = Filter.useFilter(data, standardizeFilter);
        return standardizedData;
    }

    // 数据离散化
    public Instances performDiscretization(Instances data) throws Exception {
        Discretize discretizeFilter = new Discretize();
        discretizeFilter.setInputFormat(data);
        Instances discretizedData = Filter.useFilter(data, discretizeFilter);
        return discretizedData;
    }


}
