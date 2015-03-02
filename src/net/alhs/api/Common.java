package net.alhs.api;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import net.sysmain.util.StringTools;

/**
 * ���ýӿ�
 * @author Administrator
 *
 */
public class Common{
	/**
	 * ����ָ��ʱ���ʱ���
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static int getTimeStamp(String date) throws ParseException {
		SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dd = sm.parse(date);
		return Integer.parseInt(dd.getTime()/1000+"");
	}

	/**
	 * ����ʱ�����ȡʱ��
	 * @param timestamp
	 * @return
	 * @throws ParseException
	 */
	public static String getDate(String timestamp) throws ParseException {
		SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dd = new Date();
		dd.setTime(Long.parseLong(timestamp+"000"));
		return sm.format(dd);
	}

	/**
	 * ���ָ�����ڵ�ǰ,��N��
	 * @param specifiedDay  ����
	 * @param n             �����������������
	 * @return
	 */
	public static String getSpecifiedDay(String specifiedDay,int n){ 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance(); 
		Date date=null; 
		try { 
			date = sdf.parse(specifiedDay); 
		} catch (ParseException e) { 
			e.printStackTrace(); 
		} 
		c.setTime(date); 
		int day=c.get(Calendar.DATE); 
		c.set(Calendar.DATE,day+n); 

		String dayBefore=sdf.format(c.getTime()); 
		return dayBefore; 
	} 

	public static int getMonthSpace(String start, String end)throws ParseException {
		
		SimpleDateFormat formt = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = formt.parse(start);
		Date date2 = formt.parse(end);
		
		//��У�� date1 Ҫ
		
		//�·� 
		int month = 0; 
		for ( ; ; month++) {
			 Calendar temp = Calendar.getInstance();
			 temp.setTimeInMillis(date1.getTime());
			 temp.add(Calendar.MONTH, 1);  //����ʼ�������һ���º�
			 
			 //�ж�һ���µ�����
			 if(temp.after(date2)){
				 break;  //��������������
			 }
			 if(temp.getTimeInMillis() > date2.getTime()){
				  break;
			 }
			 date1 = temp.getTime();
		}

		return month;
	}

	public static void main(String[] args)throws Exception {
		//System.out.println(getTimeStamp(new Timestamp(new Date().getTime()).toString()));
		//System.out.println(getTimeStamp(new Date().toString()));
		//1399219200	2014-05-05	1399219200	2014-05-05
		//1399125027	2014-05-03	1399125045	2014-05-03
		//System.out.println(getDate("1399125027"));
		//System.out.println(getSpecifiedDay("2014-03-01",-1));
		System.out.println(getMonthSpace("2014-06-01 00:00:00","2016-06-21 00:00:00"));
	}
}
