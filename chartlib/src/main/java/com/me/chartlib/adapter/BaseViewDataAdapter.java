package com.me.chartlib.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * 数据适配器基类--针对控件更新数据
 */
public class BaseViewDataAdapter<T> extends Observable {

    /**
     * 最大长度
     */
    public int maxDataCount = 0;

    /**
     * 保存数据
     */
    private List<T> dataList = new ArrayList();

    /**
     * 添加数据
     */
    public void addData(List<T> data) {
        dataList.clear();
//        if (data.size() > maxDataCount) {
            maxDataCount = data.size();
//        }
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void removeAt(int index) {
        dataList.remove(index);
        notifyDataSetChanged();
    }

    public void remove(List<T> data) {
        for (int i = 0; i < data.size(); i++)
            dataList.remove(data.get(i));
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return dataList;
    }

    public void notifyDataSetChanged() {
        setChanged();
        notifyObservers();
    }
}
