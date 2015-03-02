package net.alhs.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * �����׶εĴ���
 * @author Owner
 *
 */
public class Period 
{
	private String start = null;
	private String end = null;
	private int type = 0;
	
	public Period(String start, String end, int type)
	{
		this.start = start;
		this.end = end;
		this.type = type;
	}
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	
	/**
	 * 
	 * @param startDate  ��ʼʱ��
	 * @param endDate    ����ʱ��
	 * @return
	 * @throws Exception
	 */
	public YeZhJBean getPeriod(String startDate, String endDate) throws Exception
	{
		Date time1 = sdf.parse(startDate);
		Date time2 = sdf.parse(endDate);
		long l = time2.getTime()- time1.getTime();
		
		if(l <=0)
		{
			throw new Exception("��ʼ����[" + startDate + "]���ڽ�������[" + endDate + "]");
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(time1);
		cal.add(1, Calendar.YEAR);
		if(time2.getTime() >= cal.getTime().getTime())
		{
			throw new Exception("��ʼ����[" + startDate + "]�ͽ�������[" + endDate + "]�Ŀ�Ȳ��ܳ�Խ1��");
		}
		int startYear = Integer.parseInt(startDate.substring(0, 4));
		int endYear = Integer.parseInt(endDate.substring(0, 4));
		YeZhJBean bean = null;
		
		for(int index=startYear-1; index<endYear+1; index++)
		{
			String c1 = index + "-" + start;
			String c2 = null;
			if(end.compareTo(start) < 0)
			{
		        c2 = (index+1) + "-" + end;
			}
			else
			{
				c2 = index + "-" + end;
			}

			String t1 = null, t2 = null;
			
			
			/**
			 * ȡ����Ľ���
			 *   �ж���ʼʱ��
			 *     |_________________________________|
		     *   |-----------|                 
		     *             |-----------|
			 */
			if(c1.compareTo(startDate) >=0 && c1.compareTo(endDate) <= 0)
			{
				t1 = c1;
				if(c2.compareTo(endDate) < 0)
				{
					t2 = c2;
				}
				else
				{
					t2 = endDate;
				}
			}
			else if(c2.compareTo(startDate) >=0 && c2.compareTo(endDate) <= 0)
			{
				/**
				 *    �жϽ���ʱ��
				 *     |_________________________________|
			     *                                 |--------|  
			     *           
				 */
				t2 = c2;
				if(c1.compareTo(startDate) < 0)
				{
					t1 = startDate;
				}
				else
				{
					t1 = c1;
				}
			}
			else if(startDate.compareTo(c1) >=0 && startDate.compareTo(c2) <= 0)
			{

				/**
				 *    ����Ƚ϶̱��������ж�������ʼʱ��
				 *     |_______________|
			     *        |--------| 
			     *                   
				 */
				t1 = startDate;
				if(c2.compareTo(endDate) < 0)
				{
					t2 = c2;
				}
				else
				{
					t2 = endDate;
				}
			}
//			else if(endDate.compareTo(c1) >=0 && endDate.compareTo(c2) <= 0)
//			{
//
//				/**
//				 *    ����Ƚ϶̣��жϽ���ʱ�䣬��������ǰ���Ѿ�����
//				 *     |_______________|
//			     *               |--------|            
//				 */
//				t2 = endDate;
//				if(c1.compareTo(startDate) < 0)
//				{
//					t1 = startDate;
//				}
//				else
//				{
//					t1 = c1;
//				}
//			}
			if(t1 != null)
			{
				if(bean == null)
				{
					bean = new YeZhJBean();
					bean.type = this.type;
				} 
				bean.list.add(new String[]{t1, t2});
			}
		}
		
		return bean;
	}
}
