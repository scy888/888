package com.pinyougou.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_specification")
public class TbSpecification implements Serializable {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 名称
     */
    @Column(name = "spec_name")
    private String specName;

    /**
     * 添加getText方法，主要是为了支持前端select2加载数据
     * @return
     */
    public String getText(){
        return this.specName;
    }

    private static final long serialVersionUID = 1L;


    /**
     * 状态
     */
    @Column(name = "audit_status")
    private String auditStatus;

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取名称
     *
     * @return spec_name - 名称
     */
    public String getSpecName() {
        return specName;
    }

    /**
     * 设置名称
     *
     * @param specName 名称
     */
    public void setSpecName(String specName) {
        this.specName = specName;
    }

    @Override
    public String toString() {
        return "TbSpecification{" +
                "id=" + id +
                ", specName='" + specName + '\'' +
                ", auditStatus='" + auditStatus + '\'' +
                '}';
    }
}