package com.homtrip.model;



import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@SuppressWarnings("serial")
public class HotelJoinVO extends BaseEntity implements Serializable {
	
	private String guid  ;
	/**
	 * 属性
	 */
	private String sslx  ;
	/**
	 * 编号
	 */
	private String bh ;
	/**
	 * 省份
	 */
	private String sf  ;
	private String city  ;
	private String qu ;
	private String address;
	/**
	 * 业务员
	 */
	private String fzr;
	/**
	 * 手机
	 */
	private String mobile ;
	private String tel ;
	/**
	 * 开始时间
	 */
	@DateTimeFormat(iso=ISO.DATE) 
	private Date   clsj  ;
	/**
	 * 连锁名和名称
	 */
	private String mc ;
	private String dh1 ;
	private String dh2;
	private String fzrsj;
	private String fzrdh ;
	/**
	 * 联系人
	 */
	private String zrr ;
	private String zrrsj;
	private String zrrdh ;
	private String parentGuid ;
	private String parentName ;
	private String sqbmguid ;
	private String sqbm;
	private String password ;
	private Integer roomcount; 
	private Integer subcount; 
	private Integer pcount;
	private String wx ;
	private String wb;
	private String email; //EMAIL
	private String qq; //qq
	private String garden; 
	private String contentlong;
	private String step; 
	private String logo ;
	private String kouhao; 
	private String flag; 
	private String service; 
	private String level; 
	private String passport;
	private String jingdu; 
	private String weidu; 
	private String paytype;
	/**
	 * 来源
	 */
	private String source;
	private Integer status;
	private String content;
	private String od;
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getSslx() {
		return sslx;
	}
	public void setSslx(String sslx) {
		this.sslx = sslx;
	}
	public String getBh() {
		return bh;
	}
	public void setBh(String bh) {
		this.bh = bh;
	}
	public String getSf() {
		return sf;
	}
	public void setSf(String sf) {
		this.sf = sf;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getQu() {
		return qu;
	}
	public void setQu(String qu) {
		this.qu = qu;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getFzr() {
		return fzr;
	}
	public void setFzr(String fzr) {
		this.fzr = fzr;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public Date getClsj() {
		return clsj;
	}
	public void setClsj(Date clsj) {
		this.clsj = clsj;
	}
	public String getMc() {
		return mc;
	}
	public void setMc(String mc) {
		this.mc = mc;
	}
	public String getDh1() {
		return dh1;
	}
	public void setDh1(String dh1) {
		this.dh1 = dh1;
	}
	public String getDh2() {
		return dh2;
	}
	public void setDh2(String dh2) {
		this.dh2 = dh2;
	}
	public String getFzrsj() {
		return fzrsj;
	}
	public void setFzrsj(String fzrsj) {
		this.fzrsj = fzrsj;
	}
	public String getFzrdh() {
		return fzrdh;
	}
	public void setFzrdh(String fzrdh) {
		this.fzrdh = fzrdh;
	}
	public String getZrr() {
		return zrr;
	}
	public void setZrr(String zrr) {
		this.zrr = zrr;
	}
	public String getZrrsj() {
		return zrrsj;
	}
	public void setZrrsj(String zrrsj) {
		this.zrrsj = zrrsj;
	}
	public String getZrrdh() {
		return zrrdh;
	}
	public void setZrrdh(String zrrdh) {
		this.zrrdh = zrrdh;
	}
	public String getParentGuid() {
		return parentGuid;
	}
	public void setParentGuid(String parentGuid) {
		this.parentGuid = parentGuid;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getSqbmguid() {
		return sqbmguid;
	}
	public void setSqbmguid(String sqbmguid) {
		this.sqbmguid = sqbmguid;
	}
	public String getSqbm() {
		return sqbm;
	}
	public void setSqbm(String sqbm) {
		this.sqbm = sqbm;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getRoomcount() {
		return roomcount;
	}
	public void setRoomcount(Integer roomcount) {
		this.roomcount = roomcount;
	}
	public Integer getSubcount() {
		return subcount;
	}
	public void setSubcount(Integer subcount) {
		this.subcount = subcount;
	}
	public Integer getPcount() {
		return pcount;
	}
	public void setPcount(Integer pcount) {
		this.pcount = pcount;
	}
	public String getWx() {
		return wx;
	}
	public void setWx(String wx) {
		this.wx = wx;
	}
	public String getWb() {
		return wb;
	}
	public void setWb(String wb) {
		this.wb = wb;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getQq() {
		return qq;
	}
	public void setQq(String qq) {
		this.qq = qq;
	}
	public String getGarden() {
		return garden;
	}
	public void setGarden(String garden) {
		this.garden = garden;
	}
	public String getContentlong() {
		return contentlong;
	}
	public void setContentlong(String contentlong) {
		this.contentlong = contentlong;
	}
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getKouhao() {
		return kouhao;
	}
	public void setKouhao(String kouhao) {
		this.kouhao = kouhao;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getPassport() {
		return passport;
	}
	public void setPassport(String passport) {
		this.passport = passport;
	}
	public String getJingdu() {
		return jingdu;
	}
	public void setJingdu(String jingdu) {
		this.jingdu = jingdu;
	}
	public String getWeidu() {
		return weidu;
	}
	public void setWeidu(String weidu) {
		this.weidu = weidu;
	}
	public String getPaytype() {
		return paytype;
	}
	public void setPaytype(String paytype) {
		this.paytype = paytype;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getOd() {
		return od;
	}
	public void setOd(String od) {
		this.od = od;
	}
	
}
