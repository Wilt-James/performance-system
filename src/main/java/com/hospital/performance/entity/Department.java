package com.hospital.performance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hospital.performance.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 部门实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_department")
public class Department extends BaseEntity {

    /**
     * 部门编码
     */
    private String deptCode;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门层级
     */
    private Integer level;

    /**
     * 部门类型（1：临床科室，2：医技科室，3：行政科室，4：护理单元）
     */
    private Integer deptType;

    /**
     * 负责人ID
     */
    private Long leaderId;

    /**
     * 负责人姓名
     */
    private String leaderName;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 部门状态（0：禁用，1：启用）
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否为核算单元（0：否，1：是）
     */
    private Integer isAccountingUnit;

    /**
     * 成本中心编码
     */
    private String costCenterCode;

    /**
     * 预算金额
     */
    private BigDecimal budgetAmount;

    /**
     * 床位数
     */
    private Integer bedCount;

    /**
     * 人员编制数
     */
    private Integer staffCount;
}