package com.homtrip.common.model;

public class CodeItem extends CodeItemKey {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column commoncodeitem_sys.ItemName
     *
     * @mbggenerated
     */
    private String itemname;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column commoncodeitem_sys.ItemType
     *
     * @mbggenerated
     */
    private Integer itemtype;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column commoncodeitem_sys.PyName
     *
     * @mbggenerated
     */
    private String pyname;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column commoncodeitem_sys.ItemName
     *
     * @return the value of commoncodeitem_sys.ItemName
     *
     * @mbggenerated
     */
    public String getItemname() {
        return itemname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column commoncodeitem_sys.ItemName
     *
     * @param itemname the value for commoncodeitem_sys.ItemName
     *
     * @mbggenerated
     */
    public void setItemname(String itemname) {
        this.itemname = itemname == null ? null : itemname.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column commoncodeitem_sys.ItemType
     *
     * @return the value of commoncodeitem_sys.ItemType
     *
     * @mbggenerated
     */
    public Integer getItemtype() {
        return itemtype;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column commoncodeitem_sys.ItemType
     *
     * @param itemtype the value for commoncodeitem_sys.ItemType
     *
     * @mbggenerated
     */
    public void setItemtype(Integer itemtype) {
        this.itemtype = itemtype;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column commoncodeitem_sys.PyName
     *
     * @return the value of commoncodeitem_sys.PyName
     *
     * @mbggenerated
     */
    public String getPyname() {
        return pyname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column commoncodeitem_sys.PyName
     *
     * @param pyname the value for commoncodeitem_sys.PyName
     *
     * @mbggenerated
     */
    public void setPyname(String pyname) {
        this.pyname = pyname == null ? null : pyname.trim();
    }
}