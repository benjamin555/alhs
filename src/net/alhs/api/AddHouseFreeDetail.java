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
 * �Զ���ӷ��������ϸ
 * @author Administrator
 *
 */
public class AddHouseFreeDetail implements I_TemplateAction {


	private LinkedHashMap freeMap = new LinkedHashMap();

	private String begindate;
	private String enddate;

	public  int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest());   // ��װ�ػ�����

		/**
		 * ��������
		 */
		new SerialNumberAction().execute(context);


		/**
		 * �Ƿ���������ϸ
		 */
		String iszj = context.getReqParameter("iszj");                

		if(iszj==null){
			return SUCCESS;
		}

		String guid = context.getReqParameter("guid");                             //����GUID

		begindate = context.getReqParameter("fwzb_ksrq");                   //��ͬ��ʼ����
		enddate = context.getReqParameter("fwzb_jsrq");                     //��ͬ��������

		String _yzcdfy = context.getReqParameter("fwzb_yzcdzfy");                  //ҵ���е��ܷ���
		double yzcdfy = Double.parseDouble((_yzcdfy.equals(""))?"0":_yzcdfy);

		String [] yzj = context.getReqParameterValues("c_964_871_fkzq_yz");         //�����
		String [] startDate = context.getReqParameterValues("c_964_871_fkzq_ksrq"); //��ʼ����
		String [] endDate = context.getReqParameterValues("c_964_871_fkzq_jsrq");   //��������

		String fkzq = context.getReqParameter("fwzb_fkzq");                         //��������
		String fksj = context.getReqParameter("fwzb_fkrq");                         //����ʱ��
		fksj = fksj.length()==1?("0"+fksj):fksj;  //�·ݲ�0

		String fkxs = context.getReqParameter("fwzb_fkxs");     //�����Сʱ
		String fkfz = context.getReqParameter("fwzb_fkfz");     //����ķ���
		if(fkxs.length() == 1) fkxs = "0" + fkxs;
		if(fkfz.length() == 1) fkfz = "0" + fkfz;

		Connection conn = context.getConn();		

		/**
		 * ���ݺ�ͬ��ʼ���������� �õ��·��б�
		 */
		LinkedHashMap monthMap =getMonthList(begindate, enddate);

		/**
		 * 1����Ӳ�ͬʱ��ε����
		 */
		CalRent cr = new CalRent();

		if(yzj!= null && yzj.length>0){
			for (int i = 0; i < yzj.length; i++) {
				cr.addDistane(Double.parseDouble(yzj[i]), startDate[i], endDate[i]);
			}
		}

		/**
		 * ��ȡ��Ա�����ƣ���ʱ�ǿ�ϵͳ��ȡ
		 */
		/**
		String userName =  null;
		String yzGuid = context.getReqParameter("yzGuid");    //ҵ����guid
		if(yzGuid != null && StringTools.isInteger(yzGuid))    //��վ��ҵ��guid������
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

		if(userName == null) throw new Exception("���ݶ�Ӧ��ҵ����Ա������");
		**/
		
	    String userName =  context.getReqParameter("yzzl_userName");   //��Ա��

		/**
		 *  ��������𣬿۷ѣ���𸶿�
		 */
		Iterator it = monthMap.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry pairs = (Map.Entry)it.next();
			//String key = (String)pairs.getKey();
			//String value = (String)pairs.getValue();

			HouseFree hf = (HouseFree)pairs.getValue();

			/**
			 * ��������
			 */
			hf.setFksj(hf.getNyf()+"-"+fksj + " " + fkxs + ":" + fkfz + ":00");

			double zj = cr.getRent(hf.getNyf());
			/**
			 * ���(-1��δ�����·�����)
			 */
			hf.setZj(zj==-1?0:zj);    

			/**
			 * �۷�
			 */
			//1.ҵ���е�ʣ����� > �������ʱ���۷�=�������
			if(yzcdfy > hf.getZj()){
				hf.setKf(hf.getZj());
			}

			//2.����� > ҵ���е�ʣ����� >0 ʱ���۷�=ʣ����
			else if(yzcdfy > 0 && yzcdfy < hf.getZj()){
				hf.setKf(yzcdfy);
			}

			//3.ʣ�����=0ʱ���۷�=0
			else if(yzcdfy==0){
				hf.setKf(0);
			}

			//���㣺ҵ���е�ʣ�����
			yzcdfy -= hf.getKf();

			/**
			 *  ��𸶿�=���-�۷�
			 */
			hf.setZjfk(hf.getZj()-hf.getKf());
		}

		if(fkzq.equals("�¸�")){
			freeMap.putAll(monthMap);
		}
		else if(fkzq.equals("����")){
			getSeason(monthMap);
		}
		else if(fkzq.equals("���긶")){
			getHalfyear(monthMap);
		}

		/**
		 * ������ϸ
		 */
		String sql = "insert into fwzjmx(guid,houseGuid,nyf,htts,zj,kf,zjfk,fksj,UserName,Sffs) values(?,?,?,?,?,?,?,?,?,0)";
		PreparedStatement ps = conn.prepareStatement(sql);

		/**
		 * �������ã�����Ϊ������ϸGuid��
		 */
		String sql0 = "insert into fwqtfy(guid) values(?)";
		PreparedStatement ps0 = conn.prepareStatement(sql0);

		/**
		 * �޸���ɾ���
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
			ps.setString(9, userName);    //��Ա����
			ps.addBatch();

			ps0.setString(1, newGuid);
			ps0.addBatch();

			//System.out.println(fkzq+"��"+hf.getNyf()+"  "+"������"+hf.getHtts()+"  "+"���⣺"+hf.getZj()+"  "+"�۷ѣ�"+hf.getKf()+"  "+"��𸶿"+hf.getZjfk()+"   ����ʱ�䣺"+hf.getFksj());
		}
		ps.executeBatch();
		ps0.executeBatch();
//15989346035

		return SUCCESS;
	}

	/**
	 * ���ؼ��ڸ��������
	 * @param map
	 * @throws ParseException
	 */
	private void getSeason(LinkedHashMap map)throws ParseException{
		LinkedHashMap tempMap= new LinkedHashMap();  //�洢ԭʼMap

		Iterator it = map.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry pairs = (Map.Entry)it.next();
			HouseFree house = (HouseFree)pairs.getValue();

			tempMap.put(house.getNyf(),new HouseFree(house.getNyf(),0)); //����ԭʼ����

			String year = house.getNyf().substring(0, 4);
			String season = year + "-";
			String payMonth= null;
			int month = Integer.parseInt(house.getNyf().substring(5));

			if(month == 1 || month ==2 || month ==3){
				season += "һ����";
				payMonth = "01";
			}
			else if(month == 4 || month ==5 || month ==6){
				season += "������";
				payMonth = "04";
			}
			else if(month == 7 || month ==8 || month ==9){
				season += "������";
				payMonth = "07";
			}
			else if(month == 10 || month ==11 || month ==12){
				season += "�ļ���";
				payMonth = "10";
			}

			String beginYearMonth = begindate.substring(0,7);       //��ͬ��ʼ������
			String payDay = year+"-"+payMonth;                      //�������·�
			payDay = (payDay.compareTo(beginYearMonth)<0)?beginYearMonth:payDay; 

			house.setFksj(payDay+"-"+house.getFksj().substring(8));
			house.setNyf(payDay);

			/**
			 * ������ͬ���ڣ�����𣬿۷ѣ���𸶿� �ۼ�
			 */
			if(freeMap.containsKey(house.getNyf())){
				HouseFree hs = (HouseFree)freeMap.get(house.getNyf());
				int htts = hs.getHtts();       //��ͬ����
				double zj = hs.getZj();        //���
				double kf = hs.getKf();        //�۷�
				double zjfk = hs.getZjfk();    //��𸶿�
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
		 * ����ѭ�����ϲ�
		 */
		Iterator temp = tempMap.entrySet().iterator();
		while(temp.hasNext()){
			Map.Entry pairs0 = (Map.Entry)temp.next();
			HouseFree house0 = (HouseFree)pairs0.getValue();
			if(freeMap.containsKey(house0.getNyf())){     //�����и���
				HouseFree hs = (HouseFree)freeMap.get(house0.getNyf());

				tempMap.put(house0.getNyf(), hs);
			}
			else{                                         //�޸���
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
	 * ���ذ��긶�������
	 * @param map
	 * @throws ParseException
	 */
	private void getHalfyear(LinkedHashMap map)throws ParseException{

		LinkedHashMap tempMap= new LinkedHashMap();  //�洢ԭʼMap

		Iterator it = map.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry pairs = (Map.Entry)it.next();
			HouseFree house = (HouseFree)pairs.getValue();

			tempMap.put(house.getNyf(),new HouseFree(house.getNyf(),0)); //����ԭʼ����

			String year = house.getNyf().substring(0, 4);
			String halfYear = year + "-";
			String payMonth= null;
			int month = Integer.parseInt(house.getNyf().substring(5));

			if(month == 1 || month ==2 || month ==3 || month == 4 || month ==5 || month ==6){
				halfYear += "�ϰ���";
				payMonth = "02";
			}
			else{
				halfYear += "�°���";
				payMonth = "08";
			}

			String beginYearMonth = begindate.substring(0,7);    //��ͬ��ʼ������
			String endYearMonth = enddate.substring(0,7);        //��ͬ����������
			String payDay = year+"-"+payMonth;                   //��������
			/**
			 * ������·��ں�ͬ��ʼ�·�֮ǰ���򸶿��·�Ϊ��ʼ���£���һ���£�
			 */
			if(payDay.compareTo(beginYearMonth)<0){
				payDay = beginYearMonth;
			}
			/**
			 * ������·��ں�ͬ�����·�֮���򸶿��·�Ϊ�������£����һ���£�
			 */
			if(payDay.compareTo(endYearMonth)>0){
				payDay = endYearMonth;
			}
			house.setFksj(payDay+"-"+house.getFksj().substring(8));
			house.setNyf(payDay);

			/**
			 * ��ͬһ�����ڣ�����𣬿۷ѣ���𸶿� �ۼ�
			 */
			if(freeMap.containsKey(house.getNyf())){
				HouseFree hs = (HouseFree)freeMap.get(house.getNyf());
				int htts = hs.getHtts();       //��ͬ����
				double zj = hs.getZj();        //���
				double kf = hs.getKf();        //�۷�
				double zjfk = hs.getZjfk();    //��𸶿�
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
		 * ����ѭ�����ϲ�
		 */
		Iterator temp = tempMap.entrySet().iterator();
		while(temp.hasNext()){
			Map.Entry pairs0 = (Map.Entry)temp.next();
			HouseFree house0 = (HouseFree)pairs0.getValue();
			if(freeMap.containsKey(house0.getNyf())){     //�����и���
				HouseFree hs = (HouseFree)freeMap.get(house0.getNyf());

				tempMap.put(house0.getNyf(), hs);
			}
			else{                                         //�޸���
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
	 * �õ��·�����
	 * @param begindate
	 * @param enddate
	 * @return
	 * @throws ParseException
	 */
	public LinkedHashMap getMonthList(String begindate,String enddate) throws ParseException{
		SimpleDateFormat  aa = new SimpleDateFormat("yyyy-MM"); 
		String d1 = begindate.substring(0,begindate.lastIndexOf("-"));
		String d2= enddate.substring(0,enddate.lastIndexOf("-"));

		Date date1 = aa.parse(d1);     // ��ʼ����
		Date date2 = aa.parse(d2);     // ��������
		Calendar  c1 = Calendar.getInstance();
		Calendar  c2 = Calendar.getInstance();
		c1.setTime(date1); 
		c2.setTime(date2);
		c2.add(Calendar.MONTH,-1);  //�ϸ���

		//��ʼ���ڵ��·����������
		long dt = Date.parse(begindate.replace('-', '/'));
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dt);
		int maxDays = cal.getActualMaximum(Calendar.DATE);

		//��ʼ���ڵ�����
		int beginDays = maxDays-cal.get(cal.DATE)+1;

		LinkedHashMap list = new LinkedHashMap();

		/**
		 * ��ӿ�ʼ����
		 */
		list.put(d1,new HouseFree(d1,beginDays));

		while (c1.before(c2))
		{  
			c1.add(Calendar.MONTH,1);// ��ʼ���ڼ�һ����ֱ�����ڽ�������Ϊֹ
			String str =aa.format(c1.getTime());

			list.put(str,new HouseFree(str,c1.getActualMaximum(Calendar.DAY_OF_MONTH)));
		}

		/**
		 * ��ӽ������ڣ���ʼ���� �� �������� �·���ͬ����ӣ�
		 */
		if(date1.getTime()!=date2.getTime()){
			list.put(d2,new HouseFree(d2,Integer.parseInt(enddate.substring(enddate.lastIndexOf("-")+1,enddate.length()))));
		}

		return list;
	}

	/**
	 * �����
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

