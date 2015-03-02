package net.alhs.template.action.fwgl;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * ����ָ���·ݵ����
 * @author Owner
 *
 */
public class CalRent 
{
	/**
	 * ����
	 */
	private ArrayList distance = new ArrayList();
	
	/**
	 * ����ָ���·ݵ����
	 * @param date
	 *     ��ʽ��2014-10���·ݲ���10����
	 * @return
	 */
	public double getRent(String date) throws Exception
	{	
		if(distance.size() == 0) throw new Exception("��ѡʹ��addDistane���������������");
		
		long dt = Date.parse(date.replace('-', '/') + "/01");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dt);
		
		//���µ��������
		int maxDays = cal.getActualMaximum(Calendar.DATE);
		int index1 = -1, index2 = -1;
		for(int i=distance.size()-1; i>=0; i--)
		{
			DisObject o = (DisObject)distance.get(i);
			if(date.compareTo(o.endDate) <= -1 && (date.compareTo(o.startDate) >= 0 || o.startDate.substring(0,7).equals(date)))
			{//����������ڣ�����һ������
				index2 = i;
				break;
			}
		}
		if(index2 == -1) return -1;
		
		/**
		 * ��ǰ���ƣ��Ƿ����ص�
		 */
		for(int i=index2-1; i>=0; i--)
		{
			DisObject o = (DisObject)distance.get(i);
			if(date.compareTo(o.endDate) <= -1 && (date.compareTo(o.startDate) >= 0 || o.startDate.substring(0,7).equals(date)))
			{//����������ڣ���ǰ���һ��
				index1 = i;
			}
		}
		
		if(index1 == -1)
		{//û���ص�
			int startIndex = -1, endIndex = -1;
			DisObject o = (DisObject)distance.get(index2);
			
			//��ʼʱ��
			if(o.startDate.substring(0,7).equals(date))
			{//��ʼʱ���ڱ���
				startIndex = Integer.parseInt(o.startDate.substring(8));
			}
			else
			{//��ʼ����1��
				startIndex = 1;
			}
			
			//����ʱ��
			if(o.endDate.substring(0,7).equals(date))
			{//����ʱ���ڱ���
				endIndex = Integer.parseInt(o.endDate.substring(8));
			}
			else
			{//����ʱ���ڱ���ĩ
				endIndex = maxDays;
			}
				
			//������Ӧ���������
			return (endIndex - startIndex + 1) * 1d / maxDays * o.zj;
		}
		else
		{//���ص�
			double zj = 0;
			int startIndex = -1, endIndex = -1;
			//ȡÿ������Ľ��м���
			for(int i=index1; i<=index2; i++)
			{
				DisObject o = (DisObject)distance.get(i);
				if(o.startDate.substring(0, 7).equals(date))
				{//��ʼʱ���ڱ���
					startIndex = Integer.parseInt(o.startDate.substring(8));
				}
				else
				{//��ʼ����1��
					startIndex = 1;
				}
				if(o.endDate.substring(0, 7).equals(date))
				{//����ʱ���ڱ���
					endIndex = Integer.parseInt(o.endDate.substring(8));
				}
				else
				{//����ʱ���ڱ���ĩ
					endIndex = maxDays;
				}
				zj = zj + (endIndex - startIndex + 1)  * 1d/ maxDays * o.zj;
			}
			
			return zj;
		}
	}
	
	/**
	 * ע�⣺�����С�������
	 *    �����ڸ÷���������У��
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
	 * �������ʾ��
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		CalRent cr = new CalRent();
		/**
		 * 1����Ӳ�ͬʱ��ε����
		 */
		cr.addDistane(2000 ,"2014-05-18", "2014-05-28");
		cr.addDistane(3000 ,"2014-05-29", "2014-05-31");
		cr.addDistane(4000 ,"2014-06-01", "2014-08-5");
		
		/**
		 * ���ز�ͬ�·ݵĹ���
		 */
		//System.out.println("2014-04:" + cr.getRent("2014-04"));
		//System.out.println("2014-05:" + cr.getRent("2014-05"));
		//System.out.println("2014-06:" + cr.getRent("2014-06"));
		//System.out.println("2014-07:" + cr.getRent("2014-07"));
		//System.out.println("2014-08:" + cr.getRent("2014-08"));		
	}
}
