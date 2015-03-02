package net.alhs.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import net.alhs.template.action.fwgl.CalRent;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.business.template.action.SerialNumberAction;
import net.sysmain.common.ConnectionManager;
import net.sysmain.util.GUID;
import net.sysmain.util.StringTools;

/**
 * 自动添加房间租金明细
 * @author Administrator
 *
 */
public class AddHouseFreeDetail implements I_TemplateAction {


	private LinkedHashMap freeMap = new LinkedHashMap();

	private String begindate;
	private String enddate;

	public  int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest());   // 封装回话对象

		/**
		 * 产生单号
		 */
		new SerialNumberAction().execute(context);


		/**
		 * 是否产生租金明细
		 */
		String iszj = context.getReqParameter("iszj");                

		if(iszj==null){
			return SUCCESS;
		}

		String guid = context.getReqParameter("guid");                             //房间GUID

		begindate = context.getReqParameter("fwzb_ksrq");                   //合同开始日期
		enddate = context.getReqParameter("fwzb_jsrq");                     //合同结束日期

		String _yzcdfy = context.getReqParameter("fwzb_yzcdzfy");                  //业主承担总费用
		double yzcdfy = Double.parseDouble((_yzcdfy.equals(""))?"0":_yzcdfy);

		String [] yzj = context.getReqParameterValues("c_964_871_fkzq_yz");         //月租金
		String [] startDate = context.getReqParameterValues("c_964_871_fkzq_ksrq"); //开始日期
		String [] endDate = context.getReqParameterValues("c_964_871_fkzq_jsrq");   //结束日期

		String fkzq = context.getReqParameter("fwzb_fkzq");                         //付款周期
		String fksj = context.getReqParameter("fwzb_fkrq");                         //付款时间
		fksj = fksj.length()==1?("0"+fksj):fksj;  //月份补0

		String fkxs = context.getReqParameter("fwzb_fkxs");     //付款的小时
		String fkfz = context.getReqParameter("fwzb_fkfz");     //付款的分钟
		if(fkxs.length() == 1) fkxs = "0" + fkxs;
		if(fkfz.length() == 1) fkfz = "0" + fkfz;

		Connection conn = context.getConn();		

		/**
		 * 根据合同开始，结束日期 得到月份列表
		 */
		LinkedHashMap monthMap =getMonthList(begindate, enddate);

		/**
		 * 1、添加不同时间段的租金
		 */
		CalRent cr = new CalRent();

		if(yzj!= null && yzj.length>0){
			for (int i = 0; i < yzj.length; i++) {
				cr.addDistane(Double.parseDouble(yzj[i]), startDate[i], endDate[i]);
			}
		}

		/**
		 * 获取会员号名称，暂时是跨系统获取
		 */
		/**
		String userName =  null;
		String yzGuid = context.getReqParameter("yzGuid");    //业主的guid
		if(yzGuid != null && StringTools.isInteger(yzGuid))    //网站的业主guid是整数
		{
			Connection webConn = null;
			try
			{
			
				webConn = ConnectionManager.getInstance().getConnection("homtrip.xml");
				ResultSet rsMember = webConn.createStatement().executeQuery("Select username from ht_member where userid=" + yzGuid);
				if(rsMember.next()) userName = rsMember.getString("username");
			}
			catch(Exception exWeb)
			{
				exWeb.printStackTrace();
			}
			finally
			{
				ConnectionManager.close(webConn);
			}
		}

		if(userName == null) throw new Exception("房屋对应的业主会员不存在");
		**/
		
	    String userName =  context.getReqParameter("yzzl_userName");   //会员名

		/**
		 *  计算月租金，扣费，租金付款
		 */
		Iterator it = monthMap.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry pairs = (Map.Entry)it.next();
			//String key = (String)pairs.getKey();
			//String value = (String)pairs.getValue();

			HouseFree hf = (HouseFree)pairs.getValue();

			/**
			 * 付款日期
			 */
			hf.setFksj(hf.getNyf()+"-"+fksj + " " + fkxs + ":" + fkfz + ":00");

			double zj = cr.getRent(hf.getNyf());
			/**
			 * 租金(-1：未设置月份区间)
			 */
			hf.setZj(zj==-1?0:zj);    

			/**
			 * 扣费
			 */
			//1.业主承担剩余费用 > 当月租金时，扣费=当月租金
			if(yzcdfy > hf.getZj()){
				hf.setKf(hf.getZj());
			}

			//2.月租金 > 业主承担剩余费用 >0 时，扣费=剩余金额
			else if(yzcdfy > 0 && yzcdfy < hf.getZj()){
				hf.setKf(yzcdfy);
			}

			//3.剩余费用=0时，扣费=0
			else if(yzcdfy==0){
				hf.setKf(0);
			}

			//计算：业主承担剩余费用
			yzcdfy -= hf.getKf();

			/**
			 *  租金付款=租金-扣费
			 */
			hf.setZjfk(hf.getZj()-hf.getKf());
		}

		if(fkzq.equals("月付")){
			freeMap.putAll(monthMap);
		}
		else if(fkzq.equals("季付")){
			getSeason(monthMap);
		}
		else if(fkzq.equals("半年付")){
			getHalfyear(monthMap);
		}

		/**
		 * 月租明细
		 */
		String sql = "insert into fwzjmx(guid,houseGuid,nyf,htts,zj,kf,zjfk,fksj,UserName,Sffs) values(?,?,?,?,?,?,?,?,?,0)";
		PreparedStatement ps = conn.prepareStatement(sql);

		/**
		 * 其他费用（主键为月租明细Guid）
		 */
		String sql0 = "insert into fwqtfy(guid) values(?)";
		PreparedStatement ps0 = conn.prepareStatement(sql0);

		/**
		 * 修改先删后加
		 */
		if(context.getTemplatePara().getEditType() == 1)
		{
			conn.createStatement().executeUpdate("delete from fwqtfy where guid in(select guid from fwzjmx where houseGuid='"+guid+"')");
			conn.createStatement().executeUpdate("delete from fwzjmx where houseGuid='"+guid+"'");
		}		
		Iterator sea = freeMap.entrySet().iterator();
		while (sea.hasNext())
		{
			Map.Entry pairs = (Map.Entry)sea.next();
			HouseFree hf = (HouseFree)pairs.getValue();

			String newGuid = new GUID().toString();
			ps.setString(1, newGuid);
			ps.setString(2, guid);
			ps.setString(3, hf.getNyf());
			ps.setInt(4, hf.getHtts());
			ps.setDouble(5, hf.getZj());
			ps.setDouble(6, hf.getKf());
			ps.setDouble(7, hf.getZjfk());
			ps.setString(8, hf.getFksj());
			ps.setString(9, userName);    //会员名称
			ps.addBatch();

			ps0.setString(1, newGuid);
			ps0.addBatch();

			//System.out.println(fkzq+"："+hf.getNyf()+"  "+"天数："+hf.getHtts()+"  "+"月租："+hf.getZj()+"  "+"扣费："+hf.getKf()+"  "+"租金付款："+hf.getZjfk()+"   付款时间："+hf.getFksj());
		}
		ps.executeBatch();
		ps0.executeBatch();
//15989346035

		return SUCCESS;
	}

	/**
	 * 返回季节付款的区间
	 * @param map
	 * @throws ParseException
	 */
	private void getSeason(LinkedHashMap map)throws ParseException{
		LinkedHashMap tempMap= new LinkedHashMap();  //存储原始Map

		Iterator it = map.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry pairs = (Map.Entry)it.next();
			HouseFree house = (HouseFree)pairs.getValue();

			tempMap.put(house.getNyf(),new HouseFree(house.getNyf(),0)); //存入原始对象

			String year = house.getNyf().substring(0, 4);
			String season = year + "-";
			String payMonth= null;
			int month = Integer.parseInt(house.getNyf().substring(5));

			if(month == 1 || month ==2 || month ==3){
				season += "一季度";
				payMonth = "01";
			}
			else if(month == 4 || month ==5 || month ==6){
				season += "二季度";
				payMonth = "04";
			}
			else if(month == 7 || month ==8 || month ==9){
				season += "三季度";
				payMonth = "07";
			}
			else if(month == 10 || month ==11 || month ==12){
				season += "四季度";
				payMonth = "10";
			}

			String beginYearMonth = begindate.substring(0,7);       //合同开始的年月
			String payDay = year+"-"+payMonth;                      //付款年月份
			payDay = (payDay.compareTo(beginYearMonth)<0)?beginYearMonth:payDay; 

			house.setFksj(payDay+"-"+house.getFksj().substring(8));
			house.setNyf(payDay);

			/**
			 * 存在相同季节，则租金，扣费，租金付款 累加
			 */
			if(freeMap.containsKey(house.getNyf())){
				HouseFree hs = (HouseFree)freeMap.get(house.getNyf());
				int htts = hs.getHtts();       //合同天数
				double zj = hs.getZj();        //租金
				double kf = hs.getKf();        //扣费
				double zjfk = hs.getZjfk();    //租金付款
				hs.setHtts(htts+house.getHtts());
				hs.setZj(zj+house.getZj());
				hs.setKf(kf+house.getKf());
				hs.setZjfk(zjfk+house.getZjfk());
			}
			else{
				freeMap.put(house.getNyf(), house);
			}
		}

		/**
		 * 按月循环，合并
		 */
		Iterator temp = tempMap.entrySet().iterator();
		while(temp.hasNext()){
			Map.Entry pairs0 = (Map.Entry)temp.next();
			HouseFree house0 = (HouseFree)pairs0.getValue();
			if(freeMap.containsKey(house0.getNyf())){     //该月有付款
				HouseFree hs = (HouseFree)freeMap.get(house0.getNyf());

				tempMap.put(house0.getNyf(), hs);
			}
			else{                                         //无付款
				house0.setFksj((String)pairs0.getKey()+"-01");
				house0.setHtts(0);
				house0.setKf(0);
				house0.setZj(0);
				house0.setKf(0);
			}
		}

		freeMap.clear();
		freeMap.putAll(tempMap);
	}
	/**
	 * 返回半年付款的区间
	 * @param map
	 * @throws ParseException
	 */
	private void getHalfyear(LinkedHashMap map)throws ParseException{

		LinkedHashMap tempMap= new LinkedHashMap();  //存储原始Map

		Iterator it = map.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry pairs = (Map.Entry)it.next();
			HouseFree house = (HouseFree)pairs.getValue();

			tempMap.put(house.getNyf(),new HouseFree(house.getNyf(),0)); //存入原始对象

			String year = house.getNyf().substring(0, 4);
			String halfYear = year + "-";
			String payMonth= null;
			int month = Integer.parseInt(house.getNyf().substring(5));

			if(month == 1 || month ==2 || month ==3 || month == 4 || month ==5 || month ==6){
				halfYear += "上半年";
				payMonth = "02";
			}
			else{
				halfYear += "下半年";
				payMonth = "08";
			}

			String beginYearMonth = begindate.substring(0,7);    //合同开始的年月
			String endYearMonth = enddate.substring(0,7);        //合同结束的年月
			String payDay = year+"-"+payMonth;                   //付款年月
			/**
			 * 付款的月份在合同开始月份之前，则付款月份为开始年月（第一个月）
			 */
			if(payDay.compareTo(beginYearMonth)<0){
				payDay = beginYearMonth;
			}
			/**
			 * 付款的月份在合同结束月份之后，则付款月份为结束年月（最后一个月）
			 */
			if(payDay.compareTo(endYearMonth)>0){
				payDay = endYearMonth;
			}
			house.setFksj(payDay+"-"+house.getFksj().substring(8));
			house.setNyf(payDay);

			/**
			 * 在同一半年内，则租金，扣费，租金付款 累加
			 */
			if(freeMap.containsKey(house.getNyf())){
				HouseFree hs = (HouseFree)freeMap.get(house.getNyf());
				int htts = hs.getHtts();       //合同天数
				double zj = hs.getZj();        //租金
				double kf = hs.getKf();        //扣费
				double zjfk = hs.getZjfk();    //租金付款
				hs.setHtts(htts+house.getHtts());
				hs.setZj(zj+house.getZj());
				hs.setKf(kf+house.getKf());
				hs.setZjfk(zjfk+house.getZjfk());
			}
			else{
				freeMap.put(house.getNyf(), house);
			}
		}

		/**
		 * 按月循环，合并
		 */
		Iterator temp = tempMap.entrySet().iterator();
		while(temp.hasNext()){
			Map.Entry pairs0 = (Map.Entry)temp.next();
			HouseFree house0 = (HouseFree)pairs0.getValue();
			if(freeMap.containsKey(house0.getNyf())){     //该月有付款
				HouseFree hs = (HouseFree)freeMap.get(house0.getNyf());

				tempMap.put(house0.getNyf(), hs);
			}
			else{                                         //无付款
				house0.setFksj((String)pairs0.getKey()+"-01");
				house0.setHtts(0);
				house0.setKf(0);
				house0.setZj(0);
				house0.setKf(0);
			}
		}
		freeMap.clear();
		freeMap.putAll(tempMap);
	}

	/**
	 * 得到月份区间
	 * @param begindate
	 * @param enddate
	 * @return
	 * @throws ParseException
	 */
	public LinkedHashMap getMonthList(String begindate,String enddate) throws ParseException{
		SimpleDateFormat  aa = new SimpleDateFormat("yyyy-MM"); 
		String d1 = begindate.substring(0,begindate.lastIndexOf("-"));
		String d2= enddate.substring(0,enddate.lastIndexOf("-"));

		Date date1 = aa.parse(d1);     // 开始日期
		Date date2 = aa.parse(d2);     // 结束日期
		Calendar  c1 = Calendar.getInstance();
		Calendar  c2 = Calendar.getInstance();
		c1.setTime(date1); 
		c2.setTime(date2);
		c2.add(Calendar.MONTH,-1);  //上个月

		//开始日期的月份中最大天数
		long dt = Date.parse(begindate.replace('-', '/'));
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dt);
		int maxDays = cal.getActualMaximum(Calendar.DATE);

		//开始日期的天数
		int beginDays = maxDays-cal.get(cal.DATE)+1;

		LinkedHashMap list = new LinkedHashMap();

		/**
		 * 添加开始日期
		 */
		list.put(d1,new HouseFree(d1,beginDays));

		while (c1.before(c2))
		{  
			c1.add(Calendar.MONTH,1);// 开始日期加一个月直到等于结束日期为止
			String str =aa.format(c1.getTime());

			list.put(str,new HouseFree(str,c1.getActualMaximum(Calendar.DAY_OF_MONTH)));
		}

		/**
		 * 添加结束日期（开始日期 和 结束日期 月份相同则不添加）
		 */
		if(date1.getTime()!=date2.getTime()){
			list.put(d2,new HouseFree(d2,Integer.parseInt(enddate.substring(enddate.lastIndexOf("-")+1,enddate.length()))));
		}

		return list;
	}

	/**
	 * 租金类
	 * @author Administrator
	 *
	 */
	private class HouseFree{
		private String nyf = null;
		private int htts = 0;
		private double zj = 0;
		private double kf = 0;
		private double zjfk = 0;
		private String fksj = null;

		private HouseFree(String nyf,int htts){
			this.nyf = nyf;
			this.htts = htts;
		}

		public String getFksj() {
			return fksj;
		}

		public void setFksj(String fksj) {
			this.fksj = fksj;
		}

		public double getZjfk() {
			return zjfk;
		}

		public void setZjfk(double zjfk) {
			this.zjfk = zjfk;
		}

		public int getHtts() {
			return htts;
		}

		public void setHtts(int htts) {
			this.htts = htts;
		}

		public double getKf() {
			return kf;
		}

		public void setKf(double kf) {
			this.kf = kf;
		}

		public String getNyf() {
			return nyf;
		}

		public void setNyf(String nyf) {
			this.nyf = nyf;
		}

		public double getZj() {
			return zj;
		}

		public void setZj(double zj) {
			this.zj = zj;
		}
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}

