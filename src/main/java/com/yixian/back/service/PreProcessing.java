package com.yixian.back.service;

import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.instance.RemoveWithValues;

import java.util.List;
import java.util.Map;

/**
 * @author yi xian
 * @description 数据预处理类
 * @date 2023/10/23 15:59
 */
@Service
public class PreProcessing {

    public Instances handleData(Map<String, Object> data, Instances dataset) throws Exception {
        System.out.println(dataset);
        // 从data中获取componentData
        List<Map<String, Object>> componentDataList = (List<Map<String, Object>>) data.get("componentData");
        // 遍历componentDataList
        Instances newDataset = null;
        for (Map<String, Object> componentData : componentDataList) {
            // 获取propValue的值
            String propValue = (String) componentData.get("propValue");
            // 根据不同的propValue值执行不同的操作
            if ("缺失值处理".equals(propValue)) {
                // 执行归一化操作
                newDataset = MissingValueHandle(dataset);
            }else if ("数据筛选".equals(propValue)){
                newDataset = filterData(dataset,"2-4");
            }
        }
        System.out.println(newDataset);
        return newDataset;
    }

    // 缺失值处理
    public Instances MissingValueHandle(Instances dataset) throws Exception {
        System.out.println(dataset);
        // 获取数据集的实例数量
        int numInstances = dataset.numInstances();
        // 创建一个新的 Instances 对象，用于存储不包含缺失值的行
        Instances newData = new Instances(dataset, 0);
        // 检查缺失值
        System.out.println(numInstances);
        for (int i = 0; i < numInstances; i++) {
            if (!dataset.instance(i).hasMissingValue()) {
                newData.add(dataset.instance(i));
            } else {
                System.out.println("实例 " + i + " 存在缺失值");
            }
        }
        System.out.println(newData);
        return newData;
    }

    // 数据筛选
    public static Instances filterData(Instances data, String range) throws Exception {
        String[] options = {"-R", range};  // 根据传入的范围参数进行筛选
        Remove removeFilter = new Remove();
        removeFilter.setOptions(options);
        removeFilter.setInputFormat(data);
        return Filter.useFilter(data, removeFilter);
    }

}
