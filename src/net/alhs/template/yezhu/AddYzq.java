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
 * 添加业主券
 * @author Administrator
 *
 */
public class AddYzq{
	private static char [] yzqType = {'V','D','W'};   //业主券类型

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


		//开始日期：2014-08-14  结束日期：2015-08-13 (开始日期为：旺季)
		//  编号         年份       券别            有效期1                      有效期2
		//W2014001    2014       旺季券    2014-08-14-2014-10-15   2015-04-16-2015-08-13    
		//W2014002    2014      淡季券    2014-10-16-2015-04-15   
		//W2014003    2014      VIP券    2014-10-16-2015-04-15

		//开始日期：2014-02-14  结束日期：2015-02-13 (开始日期为：淡季)
		//W2014002    2014      淡季券    2014-02-14-2014-04-15   2014-10-16-2015-02-13
		//W2014003    2014      VIP券    2014-02-14-2015-04-15   2015-10-16-2015-02-13
		//W2014001    2014      旺季券    2014-04-16-2014-10-15       


		//开始日期：2014-10-14  结束日期：2016-10-13 (开始日期为：旺季)
		//W2014001    2014      旺季券    2014-10-14-2014-10-15   2015-04-16-2015-10-13   
		//D2014002    2014      淡季券    2014-10-16-2015-04-15   
		//V2014003    2014      VIP券    2014-10-16-2015-04-15
		//W2015001    2015      旺季券    2015-10-14-2015-10-15   2016-04-16-2016-10-13
		//D2015002    2015      淡季券    2015-10-16-2016-04-15
		//V2015003    2015      VIP券    2015-10-16-2016-04-15  


		ArrayList list = new ArrayList();

		//按年循环
		for (int i = beginYear; i <= endYear; i++) {
			cal.setTime(sdf.parse(begindate));
			int month = cal.get(Calendar.MONTH)+1;
			int day = cal.get(Calendar.DAY_OF_MONTH);

			//开始日期的月-日
			String monthDay = (month<10?("0"+month):month+"")+"-"+(day<10?("0"+day):day+"");

			//每段结束日期： （下一年+开始日期的月-日）减一天
			String tempDate = Common.getSpecifiedDay((i+1)+"-"+monthDay, -1);

			//每段结束日期 >= 结束日期 
			if(tempDate.compareTo(enddate)>=0){
				list.add(new String []{begindate,enddate,i+""});
				break;
			}

			list.add(new String []{begindate,tempDate,i+""});

			//每段的开始日期
			begindate = (i+1)+"-"+monthDay;

		}	

		for (int i = 0; i < list.size(); i++) {
			String [] dateArea = (String [])list.get(i);
			String d1 = dateArea[0];     //开始日期
			String d2 = dateArea[1];     //结束日期
			int year = Integer.parseInt(dateArea[2]);   //年份

			//System.out.println(d1+"-------"+d2+"---------"+year);

			//tt.length=2  
			YeZhJBean[] tt = YeZhJBean.getYeZhJBean(d1,d2);
			for (int j = 0; j < tt.length; j++) {
				String [] date = new String[4];  //存储4个有效期
				YeZhJBean yzq = tt[j];
				int type = yzq.getType();
				ArrayList lst = tt[j].getPeriods();

				//一个有效期的情况
				if(lst.size()==1){
					String[] temp = (String[])lst.get(0);
					date[0]=temp[0];
					date[1]=temp[1];
					date[2]=null;
					date[3]=null;
				}
				//2个有效期
				else if(lst.size()==2){
					String[] temp = (String[])lst.get(0);
					String[] temp1 = (String[])lst.get(1);
					date[0]=temp[0];
					date[1]=temp[1];
					date[2]=temp1[0];
					date[3]=temp1[1];
				}
				//System.out.println("年份："+year+"  "+date[0]+"-"+date[1]+"  "+date[2]+"-"+date[3]+" type:"+type);
				
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

		//格式化编号
        DecimalFormat df = new DecimalFormat("0000");
        
        
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			/**
			 * 状态：-1：失效  0：有效（不可用）  0.1:有效（可使用） 1：已使用
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
			
			

			if(type==1){         //淡季，vip
				for (int i = 1; i <= djqNum; i++) {
					ps.setString(1, new GUID().toString());
					ps.setString(5, yzqType[type]+""+year+""+df.format(i)+"");   //编号
					ps.setString(12, yzqType[type]+"");                          //类型
					ps.addBatch();
				}
				for (int i = 1; i <= vipqNum; i++) {
					ps.setString(1, new GUID().toString());
					ps.setString(5, yzqType[0]+""+year+""+df.format(i)+"");
					ps.setString(12, yzqType[0]+"");
					ps.addBatch();
				}
			}
			else if(type==2){    //旺季
				for (int i = 1; i <= wjqNum; i++) {
					ps.setString(1, new GUID().toString());
					ps.setString(5, yzqType[type]+""+year+""+df.format(i)+"");
					ps.setString(12, yzqType[type]+"");
					ps.addBatch();
				}
			}
			ps.executeBatch();
			
			/**
			 * 新增业主券后，立即更改状态
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

