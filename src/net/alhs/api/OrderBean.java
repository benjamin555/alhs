package net.alhs.api;

import java.io.Serializable;
/**
 * 订单
 * @author Administrator
 *
 */
public class OrderBean implements Serializable{
	
	private String houseGuid;         //房间GUID，用于修改房间
	
	private int mallid;               //房间id
	
	private String addtime;           //添加时间
	
	private String updatetime;        //修改时间
	
	private String sqdh;              //申请单号
	
    private String ddsx;              //订单属性
    
    private int status;               //状态
    
	private double price;             //价格
	
	private double amount;            //订单总额
	
	private double fee;               //其他费用
	
	private String seller;            //房主
	
	private String note;              //备注
	
	private int itemid;               //订单主键


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
