package net.alhs.api;

import java.io.Serializable;
/**
 * 房间
 * @author Administrator
 *
 */
public class HouseBean implements Serializable{
	
	private String guid;               //房间GUID
	
	private int itemid;                //房屋主键
	
	private int areaid;                //区域ID
	
	private String fjmc;               //房间名称
	
	private int fjfl;                  //房间分类id
	
	private double price;              //价格
	
    private String fptg;               //发票提供
    
    private String fjpz;               //房间配置
	
	private String ksrq;               //开始日期
	 
	private String jsrq;               //结束日期
	
	private String sqsj;               //申请时间
	
	private String url1;               //图片路径1
	 
	private String url2;               //图片路径2
	
	private String url3;               //图片路径3
	
	private String fjjs;               //房间介绍 
	
	private String hjjs;               //环境介绍
	
	private String yljs;               //娱乐介绍
	
	private String jtjs;               //交通介绍
	
	private String gwjs;               //购物介绍
	
	private String jdsljs;             //景点商旅介绍
	
	private int status;                //状态
	
	private String fx;                 //房型
	
	private int mj;                    //面积
	
	private int bedNo;                 //床数
	
	private double discount;           //折扣
	
	private String homeaddress;        //详细地址
	
	private int number;                //人数限制
	
	private String yzGuid;             //业主Guid
	
	private String qtmc;               //前台名称
	
	private String huayuan;            //花园
	
	private String fangxing;           //花园房型
	
	private String fangwudong;         //房屋栋
	
	private String louceng;            //楼层
	
	private String fangjianhao;        //房间号    
	
	private String fangshu;            //房属
	
	private String htlb;               //合同类别
	
	private double fcbl;               //分成比例
	
	private String bedtype;            //床型
	
	private String jingguan;           //景观
	
	private String area;               //区
	
	private String fxGuid;             //房型Guid


	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getFxGuid() {
		return fxGuid;
	}

	public void setFxGuid(String fxGuid) {
		this.fxGuid = fxGuid;
	}

	public String getBedtype() {
		return bedtype;
	}

	public void setBedtype(String bedtype) {
		this.bedtype = bedtype;
	}

	public String getJingguan() {
		return jingguan;
	}

	public void setJingguan(String jingguan) {
		this.jingguan = jingguan;
	}

	public String getFangshu() {
		return fangshu;
	}

	public void setFangshu(String fangshu) {
		this.fangshu = fangshu;
	}

	public double getFcbl() {
		return fcbl;
	}

	public void setFcbl(double fcbl) {
		this.fcbl = fcbl;
	}

	public String getHtlb() {
		return htlb;
	}

	public void setHtlb(String htlb) {
		this.htlb = htlb;
	}

	public String getYzGuid() {
		return yzGuid;
	}

	public void setYzGuid(String yzGuid) {
		this.yzGuid = yzGuid;
	}

	public int getAreaid() {
		return areaid;
	}

	public void setAreaid(int areaid) {
		this.areaid = areaid;
	}

	public int getFjfl() {
		return fjfl;
	}

	public void setFjfl(int fjfl) {
		this.fjfl = fjfl;
	}

	public String getFjjs() {
		return fjjs;
	}

	public void setFjjs(String fjjs) {
		this.fjjs = fjjs;
	}

	public String getFjmc() {
		return fjmc;
	}

	public void setFjmc(String fjmc) {
		this.fjmc = fjmc;
	}

	public String getFjpz() {
		return fjpz;
	}

	public void setFjpz(String fjpz) {
		this.fjpz = fjpz;
	}

	public String getFptg() {
		return fptg;
	}

	public void setFptg(String fptg) {
		this.fptg = fptg;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getGwjs() {
		return gwjs;
	}

	public void setGwjs(String gwjs) {
		this.gwjs = gwjs;
	}

	public String getHjjs() {
		return hjjs;
	}

	public void setHjjs(String hjjs) {
		this.hjjs = hjjs;
	}

	public int getItemid() {
		return itemid;
	}

	public void setItemid(int itemid) {
		this.itemid = itemid;
	}

	public String getJdsljs() {
		return jdsljs;
	}

	public void setJdsljs(String jdsljs) {
		this.jdsljs = jdsljs;
	}

	public String getJsrq() {
		return jsrq;
	}

	public void setJsrq(String jsrq) {
		this.jsrq = jsrq;
	}

	public String getJtjs() {
		return jtjs;
	}

	public void setJtjs(String jtjs) {
		this.jtjs = jtjs;
	}

	public String getKsrq() {
		return ksrq;
	}

	public void setKsrq(String ksrq) {
		this.ksrq = ksrq;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getSqsj() {
		return sqsj;
	}

	public void setSqsj(String sqsj) {
		this.sqsj = sqsj;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getUrl1() {
		return url1;
	}

	public void setUrl1(String url1) {
		this.url1 = url1;
	}

	public String getUrl2() {
		return url2;
	}

	public void setUrl2(String url2) {
		this.url2 = url2;
	}

	public String getUrl3() {
		return url3;
	}

	public void setUrl3(String url3) {
		this.url3 = url3;
	}

	public String getYljs() {
		return yljs;
	}

	public void setYljs(String yljs) {
		this.yljs = yljs;
	}

	public int getBedNo() {
		return bedNo;
	}

	public void setBedNo(int bedNo) {
		this.bedNo = bedNo;
	}

	public String getFx() {
		return fx;
	}

	public void setFx(String fx) {
		this.fx = fx;
	}

	public int getMj() {
		return mj;
	}

	public void setMj(int mj) {
		this.mj = mj;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public String getHomeaddress() {
		return homeaddress;
	}

	public void setHomeaddress(String homeaddress) {
		this.homeaddress = homeaddress;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void setHuayuan(String huayuan) {
		this.huayuan = huayuan;
	}

	public String getHuayuan() {
		return huayuan;
	}

	public void setFangjianhao(String fangjianhao) {
		this.fangjianhao = fangjianhao;
	}

	public String getFangjianhao() {
		return fangjianhao;
	}

	public void setFangwudong(String fangwudong) {
		this.fangwudong = fangwudong;
	}

	public String getFangwudong() {
		return fangwudong;
	}

	public void setQtmc(String qtmc) {
		this.qtmc = qtmc;
	}

	public String getQtmc() {
		return qtmc;
	}

	public String getFangxing() {
		return fangxing;
	}

	public void setFangxing(String fangxing) {
		this.fangxing = fangxing;
	}

	public String getLouceng() {
		return louceng;
	}

	public void setLouceng(String louceng) {
		this.louceng = louceng;
	}
	
}
