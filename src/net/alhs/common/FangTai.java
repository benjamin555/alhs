package net.alhs.common;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sysmain.common.ConnectionManager;
import net.sysmain.util.StringTools;

public class FangTai implements Serializable
{
	private static final long serialVersionUID = 1L;

	private Connection conn = null;

	public FangTai(Connection conn)
	{
		this.conn=conn;
	}

	private HashMap price = new HashMap(); 


	/**
	 * ���������У��Է��ͼ۸�Ļ���
	 * ����guid, map����
	 */
	private HashMap priceCache = new HashMap();

	/**
	 * ���ݷ���GUID��ʼ��
	 * @param houseGuid
	 * @throws Exception
	 * 
	 *    ���ط���guid,����guid
	 */
	private String[] priceInit(String houseGuid)throws Exception
	{
		String[] retValue = new String[2];
		PreparedStatement ps = null;
		ResultSet rs = null;

		/**
		 * ���ݷ���guid��ѯ�÷��ݵķ���ģ��Guid(����ģ�漴Ϊ����GUID)
		 */
		String sql = "select guid from house where fxGuid=(select fxGuid from house where guid = ?) and fwzt='2'";
		ps =conn.prepareStatement(sql);
		ps.setString(1, houseGuid);
		rs = ps.executeQuery();
		if(rs.next())
		{
			retValue[0] = rs.getString("guid");
		}

		/**
		 * ���ݷ���guid��ѯ�÷��ݵķ���ģ���Ӧ��rlGuid(����ģ�漴Ϊ�����rlGuid)
		 */
		sql = "select rlGuid from house where fxGuid=(select fxGuid from house where guid = ?) and fwzt='2'";
		ps =conn.prepareStatement(sql);
		ps.setString(1, houseGuid);
		rs = ps.executeQuery();
		if(rs.next())
		{
			retValue[1] = rs.getString("rlGuid");
		}

		return retValue;
	}

	/**
	 * ��ѯ����ÿ��ļ۸�begindate��enddateΪnull��ѯ���У�
	 * @param houseGuid
	 * @param begindate
	 * @param enddate
	 * @return
	 * @throws Exception
	 */
	public HashMap getDayOfPrice(String houseGuid,String begindate,String enddate)throws Exception
	{
		/**
		 * ��ʼ��
		 */
		String[] retValue = this.priceInit(houseGuid);
		String fxGuid = retValue[0];
		String rlGuid = retValue[1];

		PreparedStatement ps = null;
		ResultSet rs = null;
		LinkedHashMap map = null;

		if(fxGuid == null || fxGuid.trim().equals("")) return null;

		if((map=(LinkedHashMap)priceCache.get(fxGuid)) == null)
		{
			map = new LinkedHashMap();
		}
		else
		{//ֱ�Ӵӻ����з��ط��ͼ۸�
			return map;
		}

		try
		{
			/**
			 * ��ѯ���ݣ����ͣ��۸�
			 */
			String sql = "select djr,xjr,wjm,wjt,djm,djt,zr,gzr from fwjg where houseGuid=?";
			ps =conn.prepareStatement(sql);
			ps.setString(1, fxGuid);
			rs = ps.executeQuery();
			if(rs.next())
			{
				price.put("�����", rs.getDouble(1)+"");
				price.put("С����", rs.getDouble(2)+"");
				price.put("����ĩ", rs.getDouble(3)+"");
				price.put("������", rs.getDouble(4)+"");
				price.put("����ĩ", rs.getDouble(5)+"");
				price.put("������", rs.getDouble(6)+"");
				price.put("��ĩ", rs.getDouble(7)+"");
				price.put("������", rs.getDouble(8)+"");
			}

			sql = "select nyr,rqlx from rqsz where rlGuid = ?";
			if(begindate == null && enddate == null)
			{//���û��ָ��ʱ�䣬��������Ϊ��ǰ����֮��
				sql += "and nyr>='"+StringTools.getCurrentDateTime().substring(0, 10)+"'";
			}
			//����ָ�����ڲ�
			if(begindate != null)
			{
				sql += " and nyr >='" + begindate + "'";
			}
			if(enddate != null)
			{
				sql += " and nyr<='" + enddate + "'";
			}
			sql += " order by nyr desc";

			ps = conn.prepareStatement(sql);
			ps.setString(1, rlGuid);
			rs = ps.executeQuery();
			while(rs.next())
			{
				String nyr = rs.getString("nyr");
				String rqlx = rs.getString("rqlx");
				FangTaiObject fto = FangTaiObject.getInstance();
				fto.setNyr(nyr);
				fto.setPrice((String)price.get(rqlx));
				map.put(nyr, fto);
			}
			ConnectionManager.close(ps);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{

		}

		this.priceCache.put(fxGuid, map);

		return map;
	}


	/**
	 * ������ʼ������ָ�����͵Ķ���
	 * @param fxGuid    ����guid
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public HashMap getOrdersByHouse(String fxGuid, String beginDate, String endDate) throws Exception
	{

		HashMap orders = new HashMap();
		/**
		 * 
		 * ��������Ķ���ȷ��״̬ status
		 * 4  �Ѷ� 
		 * 5  Ԥ�� 
		 * 6  ���� 
		 *   
		 *   zcqx û��ȡ��
		 * null
		 * 0  
		 */		
		StringBuffer sqlOrder = new StringBuffer("select c.rq from fwddzb a,house b,ddfjxx c where a.houseGuid=b.guid and b.fxguid='");

		sqlOrder.append(fxGuid).append("' and a.guid=c.zbguid and a.status>='4' and a.status<='6' and (a.zcqx is null or a.zcqx='0')");
		//�򵥴���������ʱ��ıȽ�
//		if(beginDate != null && !beginDate.equals(""))
//		{//������ʼʱ��
//		sqlOrder.append(" and a.rzrq>='").append(beginDate).append("'");
//		}

//		if(endDate != null && !endDate.equals(""))
//		{//��������ʱ��
//		sqlOrder.append(" and a.rzrq<='").append(endDate).append("'");
//		}
		Statement smt = this.conn.createStatement(); 
		ResultSet rs = smt.executeQuery(sqlOrder.toString());
		while(rs.next())
		{
			String dateStr = rs.getString("rq");
			if(dateStr != null && !dateStr.trim().equals(""))
			{
				String[] temp = dateStr.split(",");
				for(int i=0; i<temp.length; i++)
				{
					/**
					 * ��ʼ��ǰ֮ǰ����Ϣ������
					 */
					if(temp[i].compareTo(beginDate) < 0) continue;

					/**
					 * �ڽ�������֮����Ϣ������
					 */
					if(endDate != null && temp[i].compareTo(endDate) > 0) continue;
					Integer count = (Integer)orders.get(temp[i]); 
					if(count != null)
					{//��ͬ���͵Ķ���+1
						int x = count.intValue()+1;
						orders.put(temp[i], new Integer(x));
					}
					else
					{//��һ�γ�ʼ��
						orders.put(temp[i], new Integer(1));
					}
				}
			}
		}
		ConnectionManager.close(smt);

		return orders;
	}


	/**
	 * �Ƿ������µ�����ѡ������ѡ�����Ƿ��Ѿ��ж�����
	 * @param houseGuid
	 * @param date
	 * @return List(�ظ��µ�������[null��ʾ���ظ�])
	 * @throws Exception
	 */
	public List isHaveOrder(String houseGuid,String [] date)throws Exception{
		/**
		 * �ӷ�����Ϣ���ѯ��ѡ������������ڣ�������ȡ����noshow��
		 */
		String sql  = "select rq from ddfjxx where zbGuid in (select guid from fwddzb where houseGuid='"+houseGuid+"' AND (zcqx IS NULL OR zcqx='0') AND (noshow IS NULL OR noshow='0'))";

		Map map = new HashMap();  //�洢����
		Statement smt = this.conn.createStatement(); 
		ResultSet rs = smt.executeQuery(sql);
		while(rs.next()){
			String [] rq = rs.getString("rq").split(",");
			for (int i = 0; i < rq.length; i++) {
				map.put(rq[i], rq[i]);
			}
		}

		List dateList = null;
		//date��ʽ��xxxx-xx-xx,xxxx-xx-xx
		for (int i = 0; i < date.length; i++) {
			String [] d = date[i].split(",");  //��������
			for(int j = 0; j < d.length; j++){
				if(map.containsKey(d[j])){
					if(dateList == null) {dateList = new ArrayList();};
					dateList.add(d[j]);
				}
			}
		}
		ConnectionManager.close(smt);

		return dateList;
	}

	/**
	 * ����ָ�����ڵĶ����������Ѷ� Ԥ�� ���� �ɶ�����Ĭ�ϵ���
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public int [] getOrderNumByDate(String date)throws Exception{
		int [] num = {0,0,0,0};
		if(date==null || date.equals("")){  //dateΪ��Ĭ�ϵ��������
			date = StringTools.getCurrentDateTime().substring(0, 10);    
		}

		String sql  = "SELECT a.status,b.rq FROM fwddzb a,ddfjxx b  WHERE b.zbGuid=a.guid  AND (a.zcqx IS NULL OR a.zcqx='0') AND (a.noshow IS NULL OR a.noshow='0') AND b.rq LIKE '%"+date+"%'";

		Statement smt = this.conn.createStatement(); 
		ResultSet rs = smt.executeQuery(sql);
		while(rs.next()){
			String status = rs.getString("a.status");         //����״̬�� 4 �Ѷ�  5 Ԥ�� 6 ���� 
			//String [] rq = rs.getString("b.rq").split(",");

			if(status.equals("4")){
				num[0]++;
			}
			else if(status.equals("5")){
				num[1]++;
			}
			else if(status.equals("6")){
				num[2]++;
			}
		}

		int count = 0;   //���۵�ҵ��������
		rs = smt.executeQuery("SELECT COUNT(*) c FROM house WHERE issale='1'");
		if(rs.next() && rs.getInt("c")>0){
			count = rs.getInt("c");
		}
		
		num[3] = count-num[0]-num[1]-num[2];
		
		ConnectionManager.close(smt);

		return num;
	}
}
