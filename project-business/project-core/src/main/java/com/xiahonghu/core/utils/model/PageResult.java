package com.xiahonghu.core.utils.model;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {
    private Integer allCount;
    private Integer currentPage;
    private Integer allPage;
    private Integer pageSize;
    private List<T> list;
    private Object sumObject;
    public PageResult(Integer allCount, Integer pageSize,Integer currentPage, List<T> list){
        this.currentPage=currentPage;
        this.allCount = allCount;
        this.pageSize =pageSize;
        if(allCount>0){
            if(allCount%pageSize>0){
                this.allPage=allCount/pageSize+1;
            }else{
                this.allPage=allCount/pageSize;
            }
        }else{
            this.allPage=0;
        }
        this.list=list;
    }

    public PageResult(Integer allCount, Integer pageSize,Integer currentPage, List<T> list,Object obj){
        this.currentPage=currentPage;
        this.allCount = allCount;
        this.pageSize =pageSize;
        if(allCount>0){
            if(allCount%pageSize>0){
                this.allPage=allCount/pageSize+1;
            }else{
                this.allPage=allCount/pageSize;
            }
        }else{
            this.allPage=0;
        }
        this.list=list;
        this.sumObject = obj;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "allCount=" + allCount +
                ", currentPage=" + currentPage +
                ", allPage=" + allPage +
                ", pageSize=" + pageSize +
                ", list=" + list +
                '}';
    }
}
