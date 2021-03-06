package com.homtrip.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class HouseCleanInfoExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table house_clean
     *
     * @mbggenerated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table house_clean
     *
     * @mbggenerated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table house_clean
     *
     * @mbggenerated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean
     *
     * @mbggenerated
     */
    public HouseCleanInfoExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean
     *
     * @mbggenerated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean
     *
     * @mbggenerated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean
     *
     * @mbggenerated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean
     *
     * @mbggenerated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean
     *
     * @mbggenerated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean
     *
     * @mbggenerated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean
     *
     * @mbggenerated
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean
     *
     * @mbggenerated
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean
     *
     * @mbggenerated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table house_clean
     *
     * @mbggenerated
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table house_clean
     *
     * @mbggenerated
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        protected void addCriterionForJDBCDate(String condition, Date value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value.getTime()), property);
        }

        protected void addCriterionForJDBCDate(String condition, List<Date> values, String property) {
            if (values == null || values.size() == 0) {
                throw new RuntimeException("Value list for " + property + " cannot be null or empty");
            }
            List<java.sql.Date> dateList = new ArrayList<java.sql.Date>();
            Iterator<Date> iter = values.iterator();
            while (iter.hasNext()) {
                dateList.add(new java.sql.Date(iter.next().getTime()));
            }
            addCriterion(condition, dateList, property);
        }

        protected void addCriterionForJDBCDate(String condition, Date value1, Date value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value1.getTime()), new java.sql.Date(value2.getTime()), property);
        }

        public Criteria andIdIsNull() {
            addCriterion("Id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("Id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(String value) {
            addCriterion("Id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(String value) {
            addCriterion("Id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(String value) {
            addCriterion("Id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(String value) {
            addCriterion("Id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(String value) {
            addCriterion("Id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(String value) {
            addCriterion("Id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLike(String value) {
            addCriterion("Id like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotLike(String value) {
            addCriterion("Id not like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<String> values) {
            addCriterion("Id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<String> values) {
            addCriterion("Id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(String value1, String value2) {
            addCriterion("Id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(String value1, String value2) {
            addCriterion("Id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andGrpidIsNull() {
            addCriterion("grpId is null");
            return (Criteria) this;
        }

        public Criteria andGrpidIsNotNull() {
            addCriterion("grpId is not null");
            return (Criteria) this;
        }

        public Criteria andGrpidEqualTo(String value) {
            addCriterion("grpId =", value, "grpid");
            return (Criteria) this;
        }

        public Criteria andGrpidNotEqualTo(String value) {
            addCriterion("grpId <>", value, "grpid");
            return (Criteria) this;
        }

        public Criteria andGrpidGreaterThan(String value) {
            addCriterion("grpId >", value, "grpid");
            return (Criteria) this;
        }

        public Criteria andGrpidGreaterThanOrEqualTo(String value) {
            addCriterion("grpId >=", value, "grpid");
            return (Criteria) this;
        }

        public Criteria andGrpidLessThan(String value) {
            addCriterion("grpId <", value, "grpid");
            return (Criteria) this;
        }

        public Criteria andGrpidLessThanOrEqualTo(String value) {
            addCriterion("grpId <=", value, "grpid");
            return (Criteria) this;
        }

        public Criteria andGrpidLike(String value) {
            addCriterion("grpId like", value, "grpid");
            return (Criteria) this;
        }

        public Criteria andGrpidNotLike(String value) {
            addCriterion("grpId not like", value, "grpid");
            return (Criteria) this;
        }

        public Criteria andGrpidIn(List<String> values) {
            addCriterion("grpId in", values, "grpid");
            return (Criteria) this;
        }

        public Criteria andGrpidNotIn(List<String> values) {
            addCriterion("grpId not in", values, "grpid");
            return (Criteria) this;
        }

        public Criteria andGrpidBetween(String value1, String value2) {
            addCriterion("grpId between", value1, value2, "grpid");
            return (Criteria) this;
        }

        public Criteria andGrpidNotBetween(String value1, String value2) {
            addCriterion("grpId not between", value1, value2, "grpid");
            return (Criteria) this;
        }

        public Criteria andHcyIsNull() {
            addCriterion("hcy is null");
            return (Criteria) this;
        }

        public Criteria andHcyIsNotNull() {
            addCriterion("hcy is not null");
            return (Criteria) this;
        }

        public Criteria andHcyEqualTo(Double value) {
            addCriterion("hcy =", value, "hcy");
            return (Criteria) this;
        }

        public Criteria andHcyNotEqualTo(Double value) {
            addCriterion("hcy <>", value, "hcy");
            return (Criteria) this;
        }

        public Criteria andHcyGreaterThan(Double value) {
            addCriterion("hcy >", value, "hcy");
            return (Criteria) this;
        }

        public Criteria andHcyGreaterThanOrEqualTo(Double value) {
            addCriterion("hcy >=", value, "hcy");
            return (Criteria) this;
        }

        public Criteria andHcyLessThan(Double value) {
            addCriterion("hcy <", value, "hcy");
            return (Criteria) this;
        }

        public Criteria andHcyLessThanOrEqualTo(Double value) {
            addCriterion("hcy <=", value, "hcy");
            return (Criteria) this;
        }

        public Criteria andHcyIn(List<Double> values) {
            addCriterion("hcy in", values, "hcy");
            return (Criteria) this;
        }

        public Criteria andHcyNotIn(List<Double> values) {
            addCriterion("hcy not in", values, "hcy");
            return (Criteria) this;
        }

        public Criteria andHcyBetween(Double value1, Double value2) {
            addCriterion("hcy between", value1, value2, "hcy");
            return (Criteria) this;
        }

        public Criteria andHcyNotBetween(Double value1, Double value2) {
            addCriterion("hcy not between", value1, value2, "hcy");
            return (Criteria) this;
        }

        public Criteria andQjztIsNull() {
            addCriterion("qjzt is null");
            return (Criteria) this;
        }

        public Criteria andQjztIsNotNull() {
            addCriterion("qjzt is not null");
            return (Criteria) this;
        }

        public Criteria andQjztEqualTo(String value) {
            addCriterion("qjzt =", value, "qjzt");
            return (Criteria) this;
        }

        public Criteria andQjztNotEqualTo(String value) {
            addCriterion("qjzt <>", value, "qjzt");
            return (Criteria) this;
        }

        public Criteria andQjztGreaterThan(String value) {
            addCriterion("qjzt >", value, "qjzt");
            return (Criteria) this;
        }

        public Criteria andQjztGreaterThanOrEqualTo(String value) {
            addCriterion("qjzt >=", value, "qjzt");
            return (Criteria) this;
        }

        public Criteria andQjztLessThan(String value) {
            addCriterion("qjzt <", value, "qjzt");
            return (Criteria) this;
        }

        public Criteria andQjztLessThanOrEqualTo(String value) {
            addCriterion("qjzt <=", value, "qjzt");
            return (Criteria) this;
        }

        public Criteria andQjztLike(String value) {
            addCriterion("qjzt like", value, "qjzt");
            return (Criteria) this;
        }

        public Criteria andQjztNotLike(String value) {
            addCriterion("qjzt not like", value, "qjzt");
            return (Criteria) this;
        }

        public Criteria andQjztIn(List<String> values) {
            addCriterion("qjzt in", values, "qjzt");
            return (Criteria) this;
        }

        public Criteria andQjztNotIn(List<String> values) {
            addCriterion("qjzt not in", values, "qjzt");
            return (Criteria) this;
        }

        public Criteria andQjztBetween(String value1, String value2) {
            addCriterion("qjzt between", value1, value2, "qjzt");
            return (Criteria) this;
        }

        public Criteria andQjztNotBetween(String value1, String value2) {
            addCriterion("qjzt not between", value1, value2, "qjzt");
            return (Criteria) this;
        }

        public Criteria andJcsjIsNull() {
            addCriterion("jcsj is null");
            return (Criteria) this;
        }

        public Criteria andJcsjIsNotNull() {
            addCriterion("jcsj is not null");
            return (Criteria) this;
        }

        public Criteria andJcsjEqualTo(Date value) {
            addCriterionForJDBCDate("jcsj =", value, "jcsj");
            return (Criteria) this;
        }

        public Criteria andJcsjNotEqualTo(Date value) {
            addCriterionForJDBCDate("jcsj <>", value, "jcsj");
            return (Criteria) this;
        }

        public Criteria andJcsjGreaterThan(Date value) {
            addCriterionForJDBCDate("jcsj >", value, "jcsj");
            return (Criteria) this;
        }

        public Criteria andJcsjGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("jcsj >=", value, "jcsj");
            return (Criteria) this;
        }

        public Criteria andJcsjLessThan(Date value) {
            addCriterionForJDBCDate("jcsj <", value, "jcsj");
            return (Criteria) this;
        }

        public Criteria andJcsjLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("jcsj <=", value, "jcsj");
            return (Criteria) this;
        }

        public Criteria andJcsjIn(List<Date> values) {
            addCriterionForJDBCDate("jcsj in", values, "jcsj");
            return (Criteria) this;
        }

        public Criteria andJcsjNotIn(List<Date> values) {
            addCriterionForJDBCDate("jcsj not in", values, "jcsj");
            return (Criteria) this;
        }

        public Criteria andJcsjBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("jcsj between", value1, value2, "jcsj");
            return (Criteria) this;
        }

        public Criteria andJcsjNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("jcsj not between", value1, value2, "jcsj");
            return (Criteria) this;
        }

        public Criteria andWtyyIsNull() {
            addCriterion("wtyy is null");
            return (Criteria) this;
        }

        public Criteria andWtyyIsNotNull() {
            addCriterion("wtyy is not null");
            return (Criteria) this;
        }

        public Criteria andWtyyEqualTo(String value) {
            addCriterion("wtyy =", value, "wtyy");
            return (Criteria) this;
        }

        public Criteria andWtyyNotEqualTo(String value) {
            addCriterion("wtyy <>", value, "wtyy");
            return (Criteria) this;
        }

        public Criteria andWtyyGreaterThan(String value) {
            addCriterion("wtyy >", value, "wtyy");
            return (Criteria) this;
        }

        public Criteria andWtyyGreaterThanOrEqualTo(String value) {
            addCriterion("wtyy >=", value, "wtyy");
            return (Criteria) this;
        }

        public Criteria andWtyyLessThan(String value) {
            addCriterion("wtyy <", value, "wtyy");
            return (Criteria) this;
        }

        public Criteria andWtyyLessThanOrEqualTo(String value) {
            addCriterion("wtyy <=", value, "wtyy");
            return (Criteria) this;
        }

        public Criteria andWtyyLike(String value) {
            addCriterion("wtyy like", value, "wtyy");
            return (Criteria) this;
        }

        public Criteria andWtyyNotLike(String value) {
            addCriterion("wtyy not like", value, "wtyy");
            return (Criteria) this;
        }

        public Criteria andWtyyIn(List<String> values) {
            addCriterion("wtyy in", values, "wtyy");
            return (Criteria) this;
        }

        public Criteria andWtyyNotIn(List<String> values) {
            addCriterion("wtyy not in", values, "wtyy");
            return (Criteria) this;
        }

        public Criteria andWtyyBetween(String value1, String value2) {
            addCriterion("wtyy between", value1, value2, "wtyy");
            return (Criteria) this;
        }

        public Criteria andWtyyNotBetween(String value1, String value2) {
            addCriterion("wtyy not between", value1, value2, "wtyy");
            return (Criteria) this;
        }

        public Criteria andYylyIsNull() {
            addCriterion("yyly is null");
            return (Criteria) this;
        }

        public Criteria andYylyIsNotNull() {
            addCriterion("yyly is not null");
            return (Criteria) this;
        }

        public Criteria andYylyEqualTo(String value) {
            addCriterion("yyly =", value, "yyly");
            return (Criteria) this;
        }

        public Criteria andYylyNotEqualTo(String value) {
            addCriterion("yyly <>", value, "yyly");
            return (Criteria) this;
        }

        public Criteria andYylyGreaterThan(String value) {
            addCriterion("yyly >", value, "yyly");
            return (Criteria) this;
        }

        public Criteria andYylyGreaterThanOrEqualTo(String value) {
            addCriterion("yyly >=", value, "yyly");
            return (Criteria) this;
        }

        public Criteria andYylyLessThan(String value) {
            addCriterion("yyly <", value, "yyly");
            return (Criteria) this;
        }

        public Criteria andYylyLessThanOrEqualTo(String value) {
            addCriterion("yyly <=", value, "yyly");
            return (Criteria) this;
        }

        public Criteria andYylyLike(String value) {
            addCriterion("yyly like", value, "yyly");
            return (Criteria) this;
        }

        public Criteria andYylyNotLike(String value) {
            addCriterion("yyly not like", value, "yyly");
            return (Criteria) this;
        }

        public Criteria andYylyIn(List<String> values) {
            addCriterion("yyly in", values, "yyly");
            return (Criteria) this;
        }

        public Criteria andYylyNotIn(List<String> values) {
            addCriterion("yyly not in", values, "yyly");
            return (Criteria) this;
        }

        public Criteria andYylyBetween(String value1, String value2) {
            addCriterion("yyly between", value1, value2, "yyly");
            return (Criteria) this;
        }

        public Criteria andYylyNotBetween(String value1, String value2) {
            addCriterion("yyly not between", value1, value2, "yyly");
            return (Criteria) this;
        }

        public Criteria andTpIsNull() {
            addCriterion("tp is null");
            return (Criteria) this;
        }

        public Criteria andTpIsNotNull() {
            addCriterion("tp is not null");
            return (Criteria) this;
        }

        public Criteria andTpEqualTo(String value) {
            addCriterion("tp =", value, "tp");
            return (Criteria) this;
        }

        public Criteria andTpNotEqualTo(String value) {
            addCriterion("tp <>", value, "tp");
            return (Criteria) this;
        }

        public Criteria andTpGreaterThan(String value) {
            addCriterion("tp >", value, "tp");
            return (Criteria) this;
        }

        public Criteria andTpGreaterThanOrEqualTo(String value) {
            addCriterion("tp >=", value, "tp");
            return (Criteria) this;
        }

        public Criteria andTpLessThan(String value) {
            addCriterion("tp <", value, "tp");
            return (Criteria) this;
        }

        public Criteria andTpLessThanOrEqualTo(String value) {
            addCriterion("tp <=", value, "tp");
            return (Criteria) this;
        }

        public Criteria andTpLike(String value) {
            addCriterion("tp like", value, "tp");
            return (Criteria) this;
        }

        public Criteria andTpNotLike(String value) {
            addCriterion("tp not like", value, "tp");
            return (Criteria) this;
        }

        public Criteria andTpIn(List<String> values) {
            addCriterion("tp in", values, "tp");
            return (Criteria) this;
        }

        public Criteria andTpNotIn(List<String> values) {
            addCriterion("tp not in", values, "tp");
            return (Criteria) this;
        }

        public Criteria andTpBetween(String value1, String value2) {
            addCriterion("tp between", value1, value2, "tp");
            return (Criteria) this;
        }

        public Criteria andTpNotBetween(String value1, String value2) {
            addCriterion("tp not between", value1, value2, "tp");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table house_clean
     *
     * @mbggenerated do_not_delete_during_merge
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table house_clean
     *
     * @mbggenerated
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value) {
            super();
            this.condition = condition;
            this.value = value;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.betweenValue = true;
        }
    }
}