package net.alhs.template.action.fwgl;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 计算指定月份的租金
 * @author Owner
 *
 */
public class CalRent 
{
	/**
	 * 区间
	 */
	private ArrayList distance = new ArrayList();
	
	/**
	 * 返回指定月份的租金
	 * @param date
	 *     格式：2014-10，月份不满10补零
	 * @return
	 */
	public double getRent(String date) throws Exception
	{	
		if(distance.size() == 0) throw new Exception("请选使用addDistane方法添加区间数据");
		
		long dt = Date.parse(date.replace('-', '/') + "/01");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dt);
		
		//本月的最大天数
		int maxDays = cal.getActualMaximum(Calendar.DATE);
		int index1 = -1, index2 = -1;
		for(int i=distance.size()-1; i>=0; i--)
		{
			DisObject o = (DisObject)distance.get(i);
			if(date.compareTo(o.endDate) <= -1 && (date.compareTo(o.startDate) >= 0 || o.startDate.substring(0,7).equals(date)))
			{//在这个区间内，最后的一个最大的
				index2 = i;
				break;
			}
		}
		if(index2 == -1) return -1;
		
		/**
		 * 向前推移，是否有重叠
		 */
		for(int i=index2-1; i>=0; i--)
		{
			DisObject o = (DisObject)distance.get(i);
			if(date.compareTo(o.endDate) <= -1 && (date.compareTo(o.startDate) >= 0 || o.startDate.substring(0,7).equals(date)))
			{//在这个区间内，最前面的一个
				index1 = i;
			}
		}
		
		if(index1 == -1)
		{//没有重叠
			int startIndex = -1, endIndex = -1;
			DisObject o = (DisObject)distance.get(index2);
			
			//开始时间
			if(o.startDate.substring(0,7).equals(date))
			{//开始时间在本月
				startIndex = Integer.parseInt(o.startDate.substring(8));
			}
			else
			{//开始日是1号
				startIndex = 1;
			}
			
			//结束时间
			if(o.endDate.substring(0,7).equals(date))
			{//结束时间在本月
				endIndex = Integer.parseInt(o.endDate.substring(8));
			}
			else
			{//结束时间在本月末
				endIndex = maxDays;
			}
				
			//返回相应比例的租金
			return (endIndex - startIndex + 1) * 1d / maxDays * o.zj;
		}
		else
		{//有重叠
			double zj = 0;
			int startIndex = -1, endIndex = -1;
			//取每个区间的进行计算
			for(int i=index1; i<=index2; i++)
			{
				DisObject o = (DisObject)distance.get(i);
				if(o.startDate.substring(0, 7).equals(date))
				{//开始时间在本月
					startIndex = Integer.parseInt(o.startDate.substring(8));
				}
				else
				{//开始日是1号
					startIndex = 1;
				}
				if(o.endDate.substring(0, 7).equals(date))
				{//结束时间在本月
					endIndex = Integer.parseInt(o.endDate.substring(8));
				}
				else
				{//结束时间在本月末
					endIndex = maxDays;
				}
				zj = zj + (endIndex - startIndex + 1)  * 1d/ maxDays * o.zj;
			}
			
			return zj;
		}
	}
	
	/**
	 * 注意：区间从小到大添加
	 *    可以在该方法中增加校验
	 * @param o
	 */
	public void addDistane(double zj ,String startDate, String endDate)
	{
		DisObject o = new DisObject();
		
		o.zj = zj;
		o.startDate = startDate;
		o.endDate = endDate;
		distance.add(o);
	}
	
	private class DisObject
	{
		double zj = 0;
		private String startDate = null;
		private String endDate = null;
	}
	
	/**
	 * 代码调用示例
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		CalRent cr = new CalRent();
		/**
		 * 1、添加不同时间段的租金
		 */
		cr.addDistane(2000 ,"2014-05-18", "2014-05-28");
		cr.addDistane(3000 ,"2014-05-29", "2014-05-31");
		cr.addDistane(4000 ,"2014-06-01", "2014-08-5");
		
		/**
		 * 返回不同月份的工资
		 */
		//System.out.println("2014-04:" + cr.getRent("2014-04"));
		//System.out.println("2014-05:" + cr.getRent("2014-05"));
		//System.out.println("2014-06:" + cr.getRent("2014-06"));
		//System.out.println("2014-07:" + cr.getRent("2014-07"));
		//System.out.println("2014-08:" + cr.getRent("2014-08"));		
	}
}
