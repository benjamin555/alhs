package net.alhs.api;

import java.io.Serializable;
/**
 * ����
 * @author Administrator
 *
 */
public class HouseBean implements Serializable{
	
	private String guid;               //����GUID
	
	private int itemid;                //��������
	
	private int areaid;                //����ID
	
	private String fjmc;               //��������
	
	private int fjfl;                  //�������id
	
	private double price;              //�۸�
	
    private String fptg;               //��Ʊ�ṩ
    
    private String fjpz;               //��������
	
	private String ksrq;               //��ʼ����
	 
	private String jsrq;               //��������
	
	private String sqsj;               //����ʱ��
	
	private String url1;               //ͼƬ·��1
	 
	private String url2;               //ͼƬ·��2
	
	private String url3;               //ͼƬ·��3
	
	private String fjjs;               //������� 
	
	private String hjjs;               //��������
	
	private String yljs;               //���ֽ���
	
	private String jtjs;               //��ͨ����
	
	private String gwjs;               //�������
	
	private String jdsljs;             //�������ý���
	
	private int status;                //״̬
	
	private String fx;                 //����
	
	private int mj;                    //���
	
	private int bedNo;                 //����
	
	private double discount;           //�ۿ�
	
	private String homeaddress;        //��ϸ��ַ
	
	private int number;                //��������
	
	private String yzGuid;             //ҵ��Guid
	
	private String qtmc;               //ǰ̨����
	
	private String huayuan;            //��԰
	
	private String fangxing;           //��԰����
	
	private String fangwudong;         //���ݶ�
	
	private String louceng;            //¥��
	
	private String fangjianhao;        //�����    
	
	private String fangshu;            //����
	
	private String htlb;               //��ͬ���
	
	private double fcbl;               //�ֳɱ���
	
	private String bedtype;            //����
	
	private String jingguan;           //����
	
	private String area;               //��
	
	private String fxGuid;             //����Guid


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
