package net.alhs.template.yezhu;

import java.util.ArrayList;

/**
 * 
 * @author 王义春
 *
 */
public class YeZhJBean 
{
	/**
	 * 格式定义，起始月日,截止日月,淡季/旺季
	 */
	private static String[][] periods = new String[][]{{"04-16","10-15", "1"}, {"10-16","04-15", "2"}};
	
	/**
	 * 旺季
	 */
	public static int TYPE_DANJI = 1;
	
	/**
	 * 淡季
	 */
	public static int TYPE_WANGJI = 2;
	
	private int type = 0;
	
	public int getType()
	{
		return this.type;
	}
	
	private ArrayList list = new ArrayList();
	
	public ArrayList getPeriods()
	{
		return list;
	}
	
	/**
	 * 产生业主卷的起止时间
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static YeZhJBean[] getYeZhJBean(String startDate, String endDate)
	{
		if(endDate.compareTo(startDate) <= 0) return null; 
		int year = Integer.parseInt(startDate.substring(0, 4));
		int year1 = Integer.parseInt(endDate.substring(0, 4));
		YeZhJBean[] beans = new YeZhJBean[2];
		
		/**
		 * 超过1年的不计算
		 */
		if(year1-year > 1) return null;
		String t5 = null;
		String t6 = null;
		
		/**
		 * 判断开始时间
		 *   重点：处在的时间段有两个卷
		 */
		int selecetedIndex = 0;
		String startMonday = startDate.substring(5);
			
		if(startMonday.compareTo(periods[0][0]) >=0 && startMonday.compareTo(periods[0][1])<=0)
		{//在同一年中
			selecetedIndex = 0;
			beans[0] = new YeZhJBean();
			beans[0].type = Integer.parseInt(periods[0][2]);
			/**
			 * 第一段
			 *   开始时间 -- 淡季或旺季的结束时间
			 */
			String t1 = startDate; 
			String t2 = year + "-" + periods[0][1];
			beans[0].list.add(new String[]{t1, t2});
			
			/**
			 * 第二段
			 *  第二年的开始 - endDate
			 */
			String t3 = (year+1) + "-" + periods[0][0];
			String t4 = endDate;
			beans[0].list.add(new String[]{t3, t4});
		}
		else
		{//跨年
			selecetedIndex = 1;				
			beans[1] = new YeZhJBean();
			beans[1].type = Integer.parseInt(periods[1][2]);
			/**
			 * 第一段
			 *   开始时间 -- 淡季或旺季的结束时间
			 */
			String t1 = startDate; 
			String t2 = (year+1) + "-" + periods[1][1];
			beans[1].list.add(new String[]{t1, t2});
			
			/**
			 * 第二段
			 *  第二年的开始 - endDate
			 */
			String t3 = (year+1) + "-" + periods[1][0];
			String t4 = endDate;
			if(t4.compareTo(t3) > 0)
			{
				beans[1].list.add(new String[]{t3, t4});
			}
		}
		
		/**
		 * 获取剩余连续的业主劵时间
		 */
		selecetedIndex++;
		if(selecetedIndex == 2) selecetedIndex = 0;
		beans[selecetedIndex] = new YeZhJBean();
		beans[selecetedIndex].type = Integer.parseInt(periods[selecetedIndex][2]);
		t5 = year + "-" + periods[selecetedIndex][0];
		if(periods[selecetedIndex][1].compareTo(periods[selecetedIndex][0]) < 0) 
		{
			year = year + 1;
		}
		t6 = year + "-" + periods[selecetedIndex][1];
		beans[selecetedIndex].list.add(new String[]{t5, t6});
		
		return beans;
	}
	
	public static void main(String[] args) throws Exception
	{
		//注释的都是有问题的
		//YeZhJBean[] tt = YeZhJBean.getYeZhJBean("2014-01-01", "2014-12-31");
		//YeZhJBean[] tt = YeZhJBean.getYeZhJBean("2014-04-01", "2014-12-31");
		//YeZhJBean[] tt = YeZhJBean.getYeZhJBean("2014-04-16", "2014-12-31");
		YeZhJBean[] tt = YeZhJBean.getYeZhJBean("2014-09-30", "2015-09-29");
		
		
		System.out.println("类型:" + tt[0].getType());
		ArrayList list = tt[0].getPeriods();
		for(int i=0; i<list.size(); i++)
		{
			String[] temp = (String[])list.get(i);
			System.out.println(temp[0] + " - " + temp[1]);
		}
		
		System.out.println("类型:" + tt[1].getType());
		list = tt[1].getPeriods();
		for(int i=0; i<list.size(); i++)
		{
			String[] temp = (String[])list.get(i);
			System.out.println(temp[0] + " - " + temp[1]);
		}
	}
}
