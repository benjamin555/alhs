package net.alhs.api;

import java.io.Serializable;
/**
 * ����
 * @author Administrator
 *
 */
public class OrderBean implements Serializable{
	
	private String houseGuid;         //����GUID�������޸ķ���
	
	private int mallid;               //����id
	
	private String addtime;           //���ʱ��
	
	private String updatetime;        //�޸�ʱ��
	
	private String sqdh;              //���뵥��
	
    private String ddsx;              //��������
    
    private int status;               //״̬
    
	private double price;             //�۸�
	
	private double amount;            //�����ܶ�
	
	private double fee;               //��������
	
	private String seller;            //����
	
	private String note;              //��ע
	
	private int itemid;               //��������


	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public String getDdsx() {
		return ddsx;
	}

	public void setDdsx(String ddsx) {
		this.ddsx = ddsx;
	}

	public double getFee() {
		return fee;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}

	public int getItemid() {
		return itemid;
	}

	public void setItemid(int itemid) {
		this.itemid = itemid;
	}

	public int getMallid() {
		return mallid;
	}

	public void setMallid(int mallid) {
		this.mallid = mallid;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getSqdh() {
		return sqdh;
	}

	public void setSqdh(String sqdh) {
		this.sqdh = sqdh;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getAddtime() {
		return addtime;
	}

	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	public String getHouseGuid() {
		return houseGuid;
	}

	public void setHouseGuid(String houseGuid) {
		this.houseGuid = houseGuid;
	}

}
