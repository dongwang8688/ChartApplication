package com.me.chartlib.utils;

import android.graphics.Path;

import java.util.HashSet;

/**
 * 路径缓存管理类
 */
public class PathCacheManager {

    /**
     * 正在使用的对象集合
     * */
    private HashSet useSet = new HashSet<Path>();

    /**
     * Path的缓存集合
     * */
    private HashSet cache = new HashSet<Path>();

    /**
     * 从缓存中取一个
     * */
    public Path get(){
        Path path;
        // 如果已经没有可用的缓存Path，创建Path，并添加到useSet
        if (cache.size() == 0) {
            path = new Path();
            useSet.add(path);
            return path;
        } else {
            // 如果缓存中有空闲的Path，取出第一个
//            path = (Path) Collection.elementAt((Iterable)cache, 0);
            path = (Path) cache.iterator().next();
            // 重置path的设置
            path.reset();
            // path从缓存中移动到使用中
            useSet.add(path);
            cache.remove(path);
            return path;
        }
    }

    /**
     * 重置缓存, 把使用中的Path添加到缓存中，并清空缓存
     * */
    public void resetCache() {
        cache.addAll(useSet);
        useSet.clear();
    }

}
