package com.homtrip.mapper;

import com.homtrip.model.HouseCleanGrp;
import com.homtrip.model.HouseCleanGrpExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface HouseCleanGrpMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean_grp
     *
     * @mbggenerated
     */
    int countByExample(HouseCleanGrpExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean_grp
     *
     * @mbggenerated
     */
    int deleteByExample(HouseCleanGrpExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean_grp
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean_grp
     *
     * @mbggenerated
     */
    int insert(HouseCleanGrp record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean_grp
     *
     * @mbggenerated
     */
    int insertSelective(HouseCleanGrp record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean_grp
     *
     * @mbggenerated
     */
    List<HouseCleanGrp> selectByExample(HouseCleanGrpExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean_grp
     *
     * @mbggenerated
     */
    HouseCleanGrp selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean_grp
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") HouseCleanGrp record, @Param("example") HouseCleanGrpExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean_grp
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") HouseCleanGrp record, @Param("example") HouseCleanGrpExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean_grp
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(HouseCleanGrp record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean_grp
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(HouseCleanGrp record);
}