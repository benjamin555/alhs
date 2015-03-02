package net.alhs.api;

import java.util.ArrayList;

/**
 * �����۸�ͬ���ӿ���,ͬ����������
 * 
 *    ִ�к������̴߳�����Ӱ������������ִ��
 * @author Owner
 *
 */
public class CalendarToWebThread
{
	private static CalendarToWebThread instance = new CalendarToWebThread();
	
	private CalendarToWebThread(){}
	
	private ArrayList orderList = new ArrayList();
	
	private ArrayList houseList = new ArrayList();
	
	private ArrayList fangxingList = new ArrayList();
	
	private InnerThread innerThread = new InnerThread();
	
	private boolean isRun = false;
	
	private boolean isInitAll = false;
	
	/**
	 * ��������б仯��ͬ���������еĿɶ�����
	 * @param orderGuid
	 */
	public static void addOrder(String orderGuid)
	{
		synchronized(instance)
		{
			instance.orderList.add(orderGuid);			
			if(!instance.isRun) 
			{
				instance.innerThread.start();
				instance.isRun = true;
			}
		}
	}
	
	/**
	 * ���ݻ�ģ���б仯��ͬ���������е�����
	 * @param houseGuid
	 */
	public static void addHouse(String houseGuid)
	{
		synchronized(instance)
		{
			instance.houseList.add(houseGuid);			
			if(!instance.isRun) 
			{
				instance.innerThread.start();
				instance.isRun = true;
			}
		}
	}
	
	/**
	 * �Է���Ϊ�������ڵ��۹���ʱ����
	 * @param fxGuid
	 */
	public static void addFangXing(String[] fxGuid)
	{
		synchronized(instance)
		{
			instance.fangxingList.add(fxGuid);			
			if(!instance.isRun) 
			{
				instance.innerThread.start();
				instance.isRun = true;
			}
		}
	}
	
	/**
	 * ��ʼ�����е���Ϣ������֮���
	 *
	 */
	public static void initAllFromToday()
	{
		synchronized(instance)
		{
			instance.isInitAll = true;
			if(!instance.isRun) 
			{
				instance.innerThread.start();
				instance.isRun = true;
			}
		}
	}
	
	class InnerThread extends Thread
	{
		public void run()
		{
			while(true)
			{
				/**
				 * �ӵ�ǰʱ���𣬳�ʼ������
				 */
				if(isInitAll)
				{
					System.out.println("��ʼ����վ���е��۵���Ϣ������");
					CalendarPriceAction action = new CalendarPriceAction();
					action.clearAndInit();
					synchronized(orderList)
					{
						orderList.clear();
					}
					
					synchronized(houseList)
					{
						houseList.clear();
					}
					
					synchronized(fangxingList)
					{
						fangxingList.clear();
					}
					synchronized(instance)
					{
						isInitAll = false;
					}
				}
				
				
				/**
				 * 1��������
				 */
				//System.out.println("orderList.size():" + orderList.size());
				if(orderList.size() > 0)
				{
					//long t1 = System.currentTimeMillis();
					CalendarPriceAction action = new CalendarPriceAction();
					action.putToWebByOrder((String)orderList.get(0));
					synchronized(orderList)
					{
						orderList.remove(0);
					}
					//System.out.println("����ͬ����ʱ��" + (System.currentTimeMillis()-t1));
				}
				
				if(houseList.size() > 0)
				{
					//long t1 = System.currentTimeMillis();
					CalendarPriceAction action = new CalendarPriceAction();
					action.putToWebByHouse((String)houseList.get(0));
					synchronized(houseList)
					{
						houseList.remove(0);
					}
					//System.out.println("����ͬ����ʱ��" + (System.currentTimeMillis()-t1));
				}
				
				if(fangxingList.size() > 0)
				{
					//long t1 = System.currentTimeMillis();
					CalendarPriceAction action = new CalendarPriceAction();
					action.putToWebByFangXing((String[])fangxingList.get(0));
					synchronized(fangxingList)
					{
						fangxingList.remove(0);
					}
					//System.out.println("����ͬ����ʱ��" + (System.currentTimeMillis()-t1));
				}
				
				try
				{
					Thread.sleep(5000);//����5��
				}
				catch(Exception ex)
				{
					;
				}
			}
		}
	} 
}
