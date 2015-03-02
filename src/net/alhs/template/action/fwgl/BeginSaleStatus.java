package net.alhs.template.action.fwgl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.alhs.common.CommonSelect;
import net.alhs.common.FangTai;
import net.alhs.common.FangTaiObject;
import net.business.engine.ListObjectBean;
import net.business.engine.ListResult;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.sysmain.util.StringTools;
/**
 * 销售状态模版显示前
 * @author Administrator
 *
 */
public class BeginSaleStatus implements I_TemplateAction
{
	private static String[] weekName = {"","日","一","二","三","四","五","六"};
	
	/**
	 * 时间、间隔显示的区间长度
	 */
	int interval = 40;
	
	/**
	 * 显示前面的日期数
	 *    ！！！只能是7的倍数
	 */
	int lastInterval = 210;
	public int execute(TemplateContext context) throws Exception
	{
		int guidIndex = context.getListObjectPara().getListObject().getListFieldByName("主键").getIndex();
		ListResult lr = context.getListResult();
		
		if(lr.length() == 0) 
		{
			context.put("dateStr", "var __lastInterval =" + this.lastInterval + "\r\n;var dateArray = []\r\n");
			return SUCCESS;
		}
		Connection conn = context.getConn();
		
		/**
		 * 在前端存储guid
		 */
		StringBuffer allGuids = new StringBuffer("var allGuids = [");
		StringBuffer sqlBuf = new StringBuffer();
		HashMap ordersMap = new HashMap();
		
		
		/**
		 * 获取房屋订单的数据
		 */
		for(int i=0; i<lr.length(); i++)
		{
			ListObjectBean line = lr.get(i);
			/**
			 * 返回某一个房屋的guid
			 */
			String houseGuid = line.get(guidIndex).getOriginalValue();
			/**
			 * 4 已收定金 
			 * 5 已预定 
			 * 6 已锁定
			 * zcqx=1 (取消的订单) noshow=1 (noshow订单)
			 */
			if(houseGuid != null && !houseGuid.trim().equals(""))
			{
				if(sqlBuf.length() > 0) sqlBuf.append(" union ");
				sqlBuf.append("select houseGuid,rzsjqj,status from fwddzb where houseGuid='").append(houseGuid).append("' and status>='4' and status<='6' and (zcqx is null or zcqx='0') and (noshow is null or noshow ='0') ");
			}
			if(i > 0) allGuids.append(",");
			allGuids.append("\"").append(houseGuid).append("\"");
			
		}
		allGuids.append("];\r\n");
		
		ResultSet rs = conn.createStatement().executeQuery(sqlBuf.toString());
		while(rs.next())
		{
			String houseGuid = rs.getString("houseGuid");
			/**
			 * 时间1,时间2,天数;时间3,时间4,天数;
			 */
			String rzsjqj = rs.getString("rzsjqj");
			String status = rs.getString("status");
			if(rzsjqj != null && !rzsjqj.trim().equals(""))
			{
				ArrayList list = (ArrayList)ordersMap.get(houseGuid);
				if(list == null) 
				{
					list = new ArrayList();
					ordersMap.put(houseGuid, list);
				}
				String[] temp = rzsjqj.split(";");
				for(int i=0; i<temp.length; i++)
				{
					String[] t= temp[i].split(",");
					t[2] = status;  //替换成状态
					/**
					 * t：时间1,时间2,订单状态(已收定金 4，已预定 5)
					 */
					list.add(t);
				}
			}
		}
		
		FangTai fangTai = new FangTai(conn);
		Calendar cal0 = Calendar.getInstance();
		/**
		 * 之前的日期
		 */
		cal0.add(Calendar.DAY_OF_MONTH, -lastInterval);
		String beginDate = StringTools.getDateByFormat(cal0.getTime(), "yyyy-MM-dd");
		cal0.add(Calendar.DAY_OF_MONTH, interval + lastInterval);
		String endDate = StringTools.getDateByFormat(cal0.getTime(), "yyyy-MM-dd");
		
		/**
		 * 每一行的数据
		 */
		StringBuffer lineDataBuf = new StringBuffer("var priceArray = new Array();\r\n");
		
		/**
		 * 返回每个房屋的价格
		    var priceArray = new Array();
		    priceArray[0] = [[100,1],[110,0],[120,0],[121,0],[122,2],[133,0],[134,0],[135,0],[136,0]];   //一行代表一个房屋的数据，如：每页12行
		    priceArray[1] = [[200,0],[210,0],[220,2],[221,0],[222,0]];
		    priceArray[2] = [[300,0],[310,1],[320,0],[321,0],[322,0],[333,0],[334,0],[335,2],[336,0]];
		**/
		int totalDays = interval + lastInterval;
		for(int i=0; i<lr.length(); i++)
		{
			ListObjectBean line = lr.get(i);
			
			/**
			 * 返回某一个房屋的guid
			 */
			String houseGuid = line.get(guidIndex).getOriginalValue();
			HashMap priceMap = fangTai.getDayOfPrice(houseGuid, beginDate, endDate);
			Calendar cal = Calendar.getInstance();
			/**
			 * 向前移动指定的日期
			 */
			cal.add(Calendar.DAY_OF_MONTH, -lastInterval);
			lineDataBuf.append("priceArray[").append(i).append("] = [");			
			for(int x=0; x<totalDays; x++)
			{
				String currentDate = StringTools.getDateByFormat(cal.getTime(), "yyyy-MM-dd");
				FangTaiObject fto = null;
				if(priceMap != null) fto = (FangTaiObject)priceMap.get(currentDate);
				cal.add(Calendar.DAY_OF_MONTH, 1);
				if(x > 0) lineDataBuf.append(",");
				if(fto != null)
				{//当日有价格
					//返回当前房屋的订单
					ArrayList list = (ArrayList)ordersMap.get(houseGuid);
					if(list == null)
					{//没有符合条件的订单
					    lineDataBuf.append("[\"").append(fto.getPrice()).append("\", 0, 0]");
					}
					else
					{
						String status = getHousetByOrderStatus(list, currentDate);
						if(status == null) status = "0";  //订单不在本日
						lineDataBuf.append("[\"").append(fto.getPrice()).append("\", ").append(status).append(", 0]");
					}
				}
				else
				{//无价格
					lineDataBuf.append("[\"\", 9, 0]");   //无效、无价格
				}
			}
			lineDataBuf.append("];\r\n");
		}		
		context.put("dateStr", lineDataBuf.insert(0, getDateString()).insert(0, allGuids.toString()).toString());
		
		
		/**
		 * 添加下拉框
		 */
		new CommonSelect().addOption(context);
		
		
		/**
		 * 统计当天的订单数量
		 */
		FangTai ft = new FangTai(conn);
		int [] num = ft.getOrderNumByDate(null);
		context.put("yidingNum", num[0]+"");
		context.put("yudingNum", num[1]+"");
		context.put("suodingNum", num[2]+"");
		context.put("kedingNum", num[3]+"");
		
		
		return SUCCESS;
	}
	
	/**
	 * 判断指定的时间是否有订单，以及订单的状态
	 * @param list
	 * @param currentDate
	 * @return
	 */
	private String getHousetByOrderStatus(ArrayList list, String currentDate)
	{
		String status = null;
		
		for(int i=0; i<list.size(); i++)
		{
			String[] temp = (String[])list.get(i);
			/**
			 * 字符串比较，currentDate>temp[0]  currentDate<=temp[1]
			 *   相等   0
			 *   小于  -1
			 *   大于  1
			 */
			if(temp[0].compareTo(currentDate)<=0 && temp[1].compareTo(currentDate)>=0)
			{
				/**
				 * 代码和js的一样，temp[2]存储的是状态
				 * 4 已收定金 
				 * 5 已预定 
				 */
				status = temp[2];
				
				return status;
			}
		}
		
		return status;
	}
	
	/**
	 * 返回今天到指定间隔时间的日期
	 * @param interval
	 * @return
	 */
	private String getDateString() throws Exception
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -lastInterval);
		StringBuffer dateBuf = new StringBuffer();
		
		dateBuf.append("[\"").append(cal.get(Calendar.MONTH)+1).append("月").append(cal.get(Calendar.DAY_OF_MONTH)).append("日");
		dateBuf.append("\",\"星期").append(weekName[cal.get(Calendar.DAY_OF_WEEK)]).append("\",\"")
		       .append(StringTools.getDateByFormat(cal.getTime(), "yyyy-MM-dd")).append("\"]");
		for(int i=1; i<this.interval + this.lastInterval; i++)
		{
			
			cal.add(Calendar.DAY_OF_MONTH, 1);
			dateBuf.append(",[\"").append(cal.get(Calendar.MONTH)+1).append("月").append(cal.get(Calendar.DAY_OF_MONTH)).append("日");
			dateBuf.append("\",\"星期").append(weekName[cal.get(Calendar.DAY_OF_WEEK)]).append("\",\"")
		       .append(StringTools.getDateByFormat(cal.getTime(), "yyyy-MM-dd")).append("\"]");
		}
		dateBuf.append("];\r\n").insert(0, "var dateArray = [");
		dateBuf.insert(0, "var __lastInterval =" + this.lastInterval + "\r\n");
		
		return dateBuf.toString();
	}
	
	public String getErrorMessage() 
	{
		return null;
	}
}
