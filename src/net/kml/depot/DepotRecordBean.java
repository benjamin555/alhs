package net.kml.depot;

import java.io.Serializable;
import java.util.Date;

/**
 * ¿â´æÁ÷Ë®
 * @author Owner
 *
 */
public class DepotRecordBean implements Serializable{
	
	private String guid;

	private String pmName;

	private String pmModel;

	private String pmType;

	private int porm;

	private String pmGuid;

	private int outOrin;

	private Date operationTime;

	private double operationNum;

	private String operationPerson;

	private int subjects;

	private String subjectsVal;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public double getOperationNum() {
		return operationNum;
	}

	public void setOperationNum(double operationNum) {
		this.operationNum = operationNum;
	}

	public String getOperationPerson() {
		return operationPerson;
	}

	public void setOperationPerson(String operationPerson) {
		this.operationPerson = operationPerson;
	}

	public Date getOperationTime() {
		return operationTime;
	}

	public void setOperationTime(Date operationTime) {
		this.operationTime = operationTime;
	}

	public int getOutOrin() {
		return outOrin;
	}

	public void setOutOrin(int outOrin) {
		this.outOrin = outOrin;
	}

	public String getPmGuid() {
		return pmGuid;
	}

	public void setPmGuid(String pmGuid) {
		this.pmGuid = pmGuid;
	}

	public String getPmModel() {
		return pmModel;
	}

	public void setPmModel(String pmModel) {
		this.pmModel = pmModel;
	}

	public String getPmName() {
		return pmName;
	}

	public void setPmName(String pmName) {
		this.pmName = pmName;
	}

	public String getPmType() {
		return pmType;
	}

	public void setPmType(String pmType) {
		this.pmType = pmType;
	}

	public int getPorm() {
		return porm;
	}

	public void setPorm(int porm) {
		this.porm = porm;
	}

	public int getSubjects() {
		return subjects;
	}

	public void setSubjects(int subjects) {
		this.subjects = subjects;
	}

	public String getSubjectsVal() {
		return subjectsVal;
	}

	public void setSubjectsVal(String subjectsVal) {
		this.subjectsVal = subjectsVal;
	}
}
