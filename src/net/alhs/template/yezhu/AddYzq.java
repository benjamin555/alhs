package net.alhs.template.yezhu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import net.alhs.api.Common;
import net.alhs.common.YeZhJBean;
import net.sysmain.common.ConnectionManager;
import net.sysmain.util.GUID;
import net.sysmain.util.StringTools;
import net.sysplat.common.Operator;


/**
 * ���ҵ��ȯ
 * @author Administrator
 *
 */
public class AddYzq{
	private static char [] yzqType = {'V','D','W'};   //ҵ��ȯ����

	private HashMap map = new HashMap();;

	public AddYzq(HashMap map){
		this.map =map;
	}

	public void addYzq() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String begindate = (String)map.get("begindate");
		String enddate = (String)map.get("enddate");


		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(begindate));
		int beginYear = cal.get(Calendar.YEAR);

		cal.setTime(sdf.parse(enddate));
		int endYear = cal.get(Calendar.YEAR);


		//��ʼ���ڣ�2014-08-14  �������ڣ�2015-08-13 (��ʼ����Ϊ������)
		//  ���         ���       ȯ��            ��Ч��1                      ��Ч��2
		//W2014001    2014       ����ȯ    2014-08-14-2014-10-15   2015-04-16-2015-08-13    
		//W2014002    2014      ����ȯ    2014-10-16-2015-04-15   
		//W2014003    2014      VIPȯ    2014-10-16-2015-04-15

		//��ʼ���ڣ�2014-02-14  �������ڣ�2015-02-13 (��ʼ����Ϊ������)
		//W2014002    2014      ����ȯ    2014-02-14-2014-04-15   2014-10-16-2015-02-13
		//W2014003    2014      VIPȯ    2014-02-14-2015-04-15   2015-10-16-2015-02-13
		//W2014001    2014      ����ȯ    2014-04-16-2014-10-15       


		//��ʼ���ڣ�2014-10-14  �������ڣ�2016-10-13 (��ʼ����Ϊ������)
		//W2014001    2014      ����ȯ    2014-10-14-2014-10-15   2015-04-16-2015-10-13   
		//D2014002    2014      ����ȯ    2014-10-16-2015-04-15   
		//V2014003    2014      VIPȯ    2014-10-16-2015-04-15
		//W2015001    2015      ����ȯ    2015-10-14-2015-10-15   2016-04-16-2016-10-13
		//D2015002    2015      ����ȯ    2015-10-16-2016-04-15
		//V2015003    2015      VIPȯ    2015-10-16-2016-04-15  


		ArrayList list = new ArrayList();

		//����ѭ��
		for (int i = beginYear; i <= endYear; i++) {
			cal.setTime(sdf.parse(begindate));
			int month = cal.get(Calendar.MONTH)+1;
			int day = cal.get(Calendar.DAY_OF_MONTH);

			//��ʼ���ڵ���-��
			String monthDay = (month<10?("0"+month):month+"")+"-"+(day<10?("0"+day):day+"");

			//ÿ�ν������ڣ� ����һ��+��ʼ���ڵ���-�գ���һ��
			String tempDate = Common.getSpecifiedDay((i+1)+"-"+monthDay, -1);

			//ÿ�ν������� >= �������� 
			if(tempDate.compareTo(enddate)>=0){
				list.add(new String []{begindate,enddate,i+""});
				break;
			}

			list.add(new String []{begindate,tempDate,i+""});

			//ÿ�εĿ�ʼ����
			begindate = (i+1)+"-"+monthDay;

		}	

		for (int i = 0; i < list.size(); i++) {
			String [] dateArea = (String [])list.get(i);
			String d1 = dateArea[0];     //��ʼ����
			String d2 = dateArea[1];     //��������
			int year = Integer.parseInt(dateArea[2]);   //���

			//System.out.println(d1+"-------"+d2+"---------"+year);

			//tt.length=2  
			YeZhJBean[] tt = YeZhJBean.getYeZhJBean(d1,d2);
			for (int j = 0; j < tt.length; j++) {
				String [] date = new String[4];  //�洢4����Ч��
				YeZhJBean yzq = tt[j];
				int type = yzq.getType();
				ArrayList lst = tt[j].getPeriods();

				//һ����Ч�ڵ����
				if(lst.size()==1){
					String[] temp = (String[])lst.get(0);
					date[0]=temp[0];
					date[1]=temp[1];
					date[2]=null;
					date[3]=null;
				}
				//2����Ч��
				else if(lst.size()==2){
					String[] temp = (String[])lst.get(0);
					String[] temp1 = (String[])lst.get(1);
					date[0]=temp[0];
					date[1]=temp[1];
					date[2]=temp1[0];
					date[3]=temp1[1];
				}
				//System.out.println("��ݣ�"+year+"  "+date[0]+"-"+date[1]+"  "+date[2]+"-"+date[3]+" type:"+type);
				
				doInsert(year, type, date);
			}	
		}
	}

	private void doInsert(int year,int type,String [] date){
		String houseGuid = (String)map.get("houseGuid");
		Operator op = (Operator)map.get("operator");
		int djqNum =  Integer.parseInt((String)map.get("djqNum"));
		int wjqNum =  Integer.parseInt((String)map.get("wjqNum"));
		int vipqNum =  Integer.parseInt((String)map.get("vipqNum"));

		//��ʽ�����
        DecimalFormat df = new DecimalFormat("0000");
        
        
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			/**
			 * ״̬��-1��ʧЧ  0����Ч�������ã�  0.1:��Ч����ʹ�ã� 1����ʹ��
			 */
			conn = ConnectionManager.getInstance().getConnection();	
			String sql = "insert into yzq(guid,sqr,sqrGuid,sqsj,houseGuid,bh,nian,zt,yxq1_ksrq,yxq1_jsrq,yxq2_ksrq,yxq2_jsrq,qb) values(?,?,?,now(),?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(2, op.getName());
			ps.setString(3, op.getLoginId());
			ps.setString(4, houseGuid);
			ps.setInt(6, year);
			ps.setString(7, "0");
			ps.setString(8, date[0]);
			ps.setString(9, date[1]);
			ps.setString(10, date[2]);
			ps.setString(11, date[3]);
			
			

			if(type==1){         //������vip
				for (int i = 1; i <= djqNum; i++) {
					ps.setString(1, new GUID().toString());
					ps.setString(5, yzqType[type]+""+year+""+df.format(i)+"");   //���
					ps.setString(12, yzqType[type]+"");                          //����
					ps.addBatch();
				}
				for (int i = 1; i <= vipqNum; i++) {
					ps.setString(1, new GUID().toString());
					ps.setString(5, yzqType[0]+""+year+""+df.format(i)+"");
					ps.setString(12, yzqType[0]+"");
					ps.addBatch();
				}
			}
			else if(type==2){    //����
				for (int i = 1; i <= wjqNum; i++) {
					ps.setString(1, new GUID().toString());
					ps.setString(5, yzqType[type]+""+year+""+df.format(i)+"");
					ps.setString(12, yzqType[type]+"");
					ps.addBatch();
				}
			}
			ps.executeBatch();
			
			/**
			 * ����ҵ��ȯ����������״̬
			 */
			YeZhuTotalInfo info = new YeZhuTotalInfo();
			info.yzqOutTime(houseGuid);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			ConnectionManager.close(conn);
		}
	}
}

