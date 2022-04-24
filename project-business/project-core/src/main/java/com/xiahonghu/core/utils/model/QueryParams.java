package com.xiahonghu.core.utils.model;



import com.xiahonghu.core.utils.exception.Assert;
import com.xiahonghu.core.utils.exception.RRException;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QueryParams implements Serializable {

    private Integer relatedId;

    private Integer userId;

    private Integer type;

    private String createTime;//用户注册日期

    private String timeStart;//开始时间

    private String timeEnd;//结束时间

    private Short status;//状态

    private String email;//用户账号
    // 模糊查询字段
    private String matcherField;
    //当前页码
    private Integer currentPage;
    //每页条数
    private Integer pageSize;
    // 偏移量
    private Long offset;
    // 排序规则
    private String sortRule;

    public void defaultCheckAllFields(List<String> sortStrList) {
        checkAllFields(sortStrList, "id desc");
    }

    public void checkAllFields(List<String> sortStrList, String defaultSortFields) {
        if (this.sortRule == null) {
            this.sortRule = defaultSortFields;
        } else {
            boolean threwError = true;
            String waitCheckStr = this.sortRule.toUpperCase();
            if (waitCheckStr.endsWith(" DESC")) {
                waitCheckStr = waitCheckStr.split(" DESC")[0];
            }
            if (waitCheckStr.endsWith(" ASC")) {
                waitCheckStr = waitCheckStr.split(" ASC")[0];
            }
            for (String sortStr : sortStrList) {
                if (sortStr.equalsIgnoreCase(waitCheckStr)) {
                    threwError = false;
                    break;
                }
            }
            if (threwError) {
                throw new RRException("排序字段不匹配");
            }
        }
        if (this.matcherField != null) {
            checkLikeField(this.matcherField);
        }
        if (this.currentPage == null) {
            this.currentPage = 1;
        }
        if (this.pageSize == null) {
            this.pageSize = 10;
        }
        this.offset = (this.currentPage - 1) * this.pageSize.longValue();
    }

    private static void checkLikeField(String matcherField) {
        Assert.isBlank(matcherField, "不允许为空字符");
        Assert.handJudge(matcherField.startsWith("%"), "检索起始字符不能为%");
        Assert.handJudge(matcherField.endsWith("%"), "检索结束字符不能为%");
    }
}
