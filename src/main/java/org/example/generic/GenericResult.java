package org.example.generic;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 通用结果封装类，用于API响应数据的统一封装
 * 解决了序列化和不可变列表操作的问题
 */
@Data
public class GenericResult implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 成功状态码 */
    public static final String SUCCESS_CODE = "0";
    /** 成功但包含警告的状态码 */
    public static final String SUCCESS_WARNING_CODE = "100";
    /** 错误状态码 */
    public static final String ERROR_CODE = "1";

    private String flag;          // 状态码
    private String prompt;        // 提示信息
    private int rows;             // 数据行数
    private String times;         // 执行耗时
    private String errorLevel;    // 错误等级
    private List<Map<String, Object>> dataList; // 主数据列表
    private Map<String, Object> extData; // 扩展数据
    private Map<String, Object> firstData; // 第一条数据

    // 构造函数
    public GenericResult() {
        this.initSuccessState();
        this.dataList = new ArrayList<>();
        this.extData = new HashMap<>();
        this.firstData = new HashMap<>();
    }

    public GenericResult(String flag, String prompt) {
        this.flag = flag;
        this.prompt = prompt;
        this.dataList = new ArrayList<>();
        this.extData = new HashMap<>();
        this.firstData = new HashMap<>();
    }

    /**
     * 从MyBatis查询结果(HashMap)初始化
     */
    public GenericResult(Map resultMap) {
        this();
        if (resultMap != null) {
            this.dataList.add(resultMap);
            this.firstData = resultMap;
            this.rows = 1;
        }
    }

    /**
     * 从MyBatis查询结果(List<HashMap>)初始化
     */
    public GenericResult(List<Map<String, Object>> resultList) {
        this();
        if (CollectionUtil.isNotEmpty(resultList)) {
            this.dataList = new ArrayList<>(resultList);
            this.firstData = resultList.get(0);
            this.rows = resultList.size();
        }
    }

    // 核心状态判断方法
    public boolean isSuccess() {
        return SUCCESS_CODE.equals(flag) || SUCCESS_WARNING_CODE.equals(flag);
    }

    public boolean isError() {
        return !isSuccess();
    }

    public void setSuccess() {
        this.initSuccessState();
    }

    public void setError(String errorCode, String errorMsg) {
        this.flag = errorCode;
        this.prompt = errorMsg;
        this.errorLevel = "ERROR";
        this.dataList.clear();
        this.firstData.clear();
    }

    // 数据操作方法
    /** 添加单条Map数据 */
    public void addData(Map<String, Object> dataMap) {
        if (dataMap != null) {
            this.dataList.add(dataMap);
            this.rows = dataList.size();
            this.firstData = dataList.get(0);
        }
    }

    /** 添加Map列表数据 */
    public void addDataList(List<Map<String, Object>> dataList) {
        if (CollectionUtil.isNotEmpty(dataList)) {
            this.dataList.addAll(dataList);
            this.rows = dataList.size();
            if (CollectionUtil.isNotEmpty(this.dataList)) {
                this.firstData = this.dataList.get(0);
            }
        }
    }

    /** 获取第一条数据 */
    public Map<String, Object> getFirstData() {
        return CollectionUtil.isNotEmpty(dataList) ? dataList.get(0) : new HashMap<>();
    }

    /** 设置第一条数据 */
    public void setFirstData(Map<String, Object> firstData) {
        this.firstData = firstData;
        if (CollectionUtil.isNotEmpty(dataList)) {
            dataList.set(0, firstData);
        } else if (firstData != null) {
            dataList.add(firstData);
        }
    }

    /** 获取数据列表（返回副本，保持不可变性） */
    public List<Map<String, Object>> getDataList() {
        return new ArrayList<>(dataList);
    }

    /** 设置数据列表 */
    public void setDataList(List<Map<String, Object>> dataList) {
        this.dataList = CollectionUtil.isEmpty(dataList) ?
                new ArrayList<>() : new ArrayList<>(dataList);
        this.rows = dataList.size();
        if (CollectionUtil.isNotEmpty(this.dataList)) {
            this.firstData = this.dataList.get(0);
        } else {
            this.firstData = new HashMap<>();
        }
    }

    public void setExtData(Map<String, Object> extData) {
        this.extData = Optional.ofNullable(extData).orElse(new HashMap<>());
    }

    // 扩展数据操作
    public void putExtData(String key, Object value) {
        this.extData.put(key, value);
    }

    public void clear() {
        this.dataList.clear();
        this.extData.clear();
        this.firstData.clear();
        this.initSuccessState();
    }

    private void initSuccessState() {
        this.flag = SUCCESS_CODE;
        this.prompt = "操作成功";
        this.errorLevel = "INFO";
    }

    // 重写方法
    @Override
    public String toString() {
        return "GenericResult{" +
                "flag='" + flag + '\'' +
                ", prompt='" + prompt + '\'' +
                ", rows=" + rows +
                ", times='" + times + '\'' +
                ", errorLevel='" + errorLevel + '\'' +
                ", dataList=" + dataList +
                ", extData=" + extData +
                ", firstData=" + firstData +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenericResult that = (GenericResult) o;
        return rows == that.rows &&
                Objects.equals(flag, that.flag) &&
                Objects.equals(prompt, that.prompt) &&
                Objects.equals(times, that.times) &&
                Objects.equals(errorLevel, that.errorLevel) &&
                Objects.equals(dataList, that.dataList) &&
                Objects.equals(extData, that.extData) &&
                Objects.equals(firstData, that.firstData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flag, prompt, rows, times, errorLevel, dataList, extData, firstData);
    }
}