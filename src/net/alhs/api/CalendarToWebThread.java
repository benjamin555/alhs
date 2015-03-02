package net.alhs.api;

import java.util.ArrayList;

/**
 * 日历价格同步接口类,同步至家游网
 * 
 *    执行后启动线程处理，不影响调用主程序的执行
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
	 * 订单如果有变化，同步家游网中的可订数据
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
	 * 房屋或模板有变化，同步家游网中的数据
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
	 * 以房型为基础，在调价管理时调用
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
	 * 初始化所有的信息，今天之后的
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
				 * 从当前时间起，初始化所有
				 */
				if(isInitAll)
				{
					System.out.println("初始化网站所有调价的信息。。。");
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
				 * 1、处理订单
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
					//System.out.println("订单同步耗时：" + (System.currentTimeMillis()-t1));
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
					//System.out.println("房屋同步耗时：" + (System.currentTimeMillis()-t1));
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
					//System.out.println("房屋同步耗时：" + (System.currentTimeMillis()-t1));
				}
				
				try
				{
					Thread.sleep(5000);//休眠5秒
				}
				catch(Exception ex)
				{
					;
				}
			}
		}
	} 
}
