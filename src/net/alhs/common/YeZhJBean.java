package net.alhs.common;

import java.util.ArrayList;

/**
 * 
 * @author ���崺
 *
 */
public class YeZhJBean 
{
	/**
	 * ��ʽ���壺��ʼ����,��ֹ����,����/����
	 * ֧��һ���ڶ���׶εĶ���
	 */
	private static String[][] periods = new String[][]{{"04-16","10-15", "2"}, {"10-16","04-15", "1"}};
	
	protected int type = 0;
	
	public int getType()
	{
		return this.type;
	}
	
	protected ArrayList list = new ArrayList();
	
	public ArrayList getPeriods()
	{
		return list;
	}
	
	
	/**
	 * ����ҵ�������ֹʱ��
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static YeZhJBean[] getYeZhJBean(String startDate, String endDate) throws Exception
	{
		ArrayList list = new ArrayList();
		for(int i=0; i<periods.length; i++)
		{
			Period period = new Period(periods[i][0], periods[i][1], Integer.parseInt(periods[i][2]));
			
			YeZhJBean bean = period.getPeriod(startDate, endDate);
			if(bean != null) list.add(bean);
		}
		if(list.size() > 0)
		{
			YeZhJBean[] beans = new YeZhJBean[list.size()];
			beans = (YeZhJBean[])list.toArray(beans);
			
			return beans;
		}
		
		return null;
	}
	
	public static void main(String[] args) throws Exception
	{
		String[] t = new String[]{"2014-01-01", "2014-03-31"}; 
		YeZhJBean[] tt = YeZhJBean.getYeZhJBean(t[0], t[1]);
		//System.out.println(t[0] + " - " + t[1]);
		
		for(int x=0; x<tt.length; x++)
		{
			System.out.println("����:" + tt[x].getType());
			ArrayList list = tt[x].getPeriods();
			for(int i=0; i<list.size(); i++)
			{
				String[] temp = (String[])list.get(i);
				System.out.println(temp[0] + " - " + temp[1]);
			}
		}
	}
}
