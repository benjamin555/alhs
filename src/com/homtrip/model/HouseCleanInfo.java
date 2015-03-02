package com.homtrip.model;

import java.util.Date;

public class HouseCleanInfo {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column house_clean.Id
     *
     * @mbggenerated
     */
    private String id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column house_clean.grpId
     *
     * @mbggenerated
     */
    private String grpid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column house_clean.hcy
     *
     * @mbggenerated
     */
    private Double hcy;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column house_clean.qjzt
     *
     * @mbggenerated
     */
    private String qjzt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column house_clean.jcsj
     *
     * @mbggenerated
     */
    private Date jcsj;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column house_clean.wtyy
     *
     * @mbggenerated
     */
    private String wtyy;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column house_clean.yyly
     *
     * @mbggenerated
     */
    private String yyly;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column house_clean.tp
     *
     * @mbggenerated
     */
    private String tp;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column house_clean.Id
     *
     * @return the value of house_clean.Id
     *
     * @mbggenerated
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column house_clean.Id
     *
     * @param id the value for house_clean.Id
     *
     * @mbggenerated
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column house_clean.grpId
     *
     * @return the value of house_clean.grpId
     *
     * @mbggenerated
     */
    public String getGrpid() {
        return grpid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column house_clean.grpId
     *
     * @param grpid the value for house_clean.grpId
     *
     * @mbggenerated
     */
    public void setGrpid(String grpid) {
        this.grpid = grpid == null ? null : grpid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column house_clean.hcy
     *
     * @return the value of house_clean.hcy
     *
     * @mbggenerated
     */
    public Double getHcy() {
        return hcy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column house_clean.hcy
     *
     * @param hcy the value for house_clean.hcy
     *
     * @mbggenerated
     */
    public void setHcy(Double hcy) {
        this.hcy = hcy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column house_clean.qjzt
     *
     * @return the value of house_clean.qjzt
     *
     * @mbggenerated
     */
    public String getQjzt() {
        return qjzt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column house_clean.qjzt
     *
     * @param qjzt the value for house_clean.qjzt
     *
     * @mbggenerated
     */
    public void setQjzt(String qjzt) {
        this.qjzt = qjzt == null ? null : qjzt.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column house_clean.jcsj
     *
     * @return the value of house_clean.jcsj
     *
     * @mbggenerated
     */
    public Date getJcsj() {
        return jcsj;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column house_clean.jcsj
     *
     * @param jcsj the value for house_clean.jcsj
     *
     * @mbggenerated
     */
    public void setJcsj(Date jcsj) {
        this.jcsj = jcsj;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column house_clean.wtyy
     *
     * @return the value of house_clean.wtyy
     *
     * @mbggenerated
     */
    public String getWtyy() {
        return wtyy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column house_clean.wtyy
     *
     * @param wtyy the value for house_clean.wtyy
     *
     * @mbggenerated
     */
    public void setWtyy(String wtyy) {
        this.wtyy = wtyy == null ? null : wtyy.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column house_clean.yyly
     *
     * @return the value of house_clean.yyly
     *
     * @mbggenerated
     */
    public String getYyly() {
        return yyly;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column house_clean.yyly
     *
     * @param yyly the value for house_clean.yyly
     *
     * @mbggenerated
     */
    public void setYyly(String yyly) {
        this.yyly = yyly == null ? null : yyly.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column house_clean.tp
     *
     * @return the value of house_clean.tp
     *
     * @mbggenerated
     */
    public String getTp() {
        return tp;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column house_clean.tp
     *
     * @param tp the value for house_clean.tp
     *
     * @mbggenerated
     */
    public void setTp(String tp) {
        this.tp = tp == null ? null : tp.trim();
    }
    
    private HouseCleanGrp cleanGrp;

	public HouseCleanGrp getCleanGrp() {
		return cleanGrp;
	}

	public void setCleanGrp(HouseCleanGrp cleanGrp) {
		this.cleanGrp = cleanGrp;
	}
    
     
}