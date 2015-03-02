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
 * ����״̬ģ����ʾǰ
 * @author Administrator
 *
 */
public class BeginSaleStatus implements I_TemplateAction
{
	private static String[] weekName = {"","��","һ","��","��","��","��","��"};
	
	/**
	 * ʱ�䡢�����ʾ�����䳤��
	 */
	int interval = 40;
	
	/**
	 * ��ʾǰ���������
	 *    ������ֻ����7�ı���
	 */
	int lastInterval = 210;
	public int execute(TemplateContext context) throws Exception
	{
		int guidIndex = context.getListObjectPara().getListObject().getListFieldByName("����").getIndex();
		ListResult lr = context.getListResult();
		
		if(lr.length() == 0) 
		{
			context.put("dateStr", "var __lastInterval =" + this.lastInterval + "\r\n;var dateArray = []\r\n");
			return SUCCESS;
		}
		Connection conn = context.getConn();
		
		/**
		 * ��ǰ�˴洢guid
		 */
		StringBuffer allGuids = new StringBuffer("var allGuids = [");
		StringBuffer sqlBuf = new StringBuffer();
		HashMap ordersMap = new HashMap();
		
		
		/**
		 * ��ȡ���ݶ���������
		 */
		for(int i=0; i<lr.length(); i++)
		{
			ListObjectBean line = lr.get(i);
			/**
			 * ����ĳһ�����ݵ�guid
			 */
			String houseGuid = line.get(guidIndex).getOriginalValue();
			/**
			 * 4 ���ն��� 
			 * 5 ��Ԥ�� 
			 * 6 ������
			 * zcqx=1 (ȡ���Ķ���) noshow=1 (noshow����)
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
			 * ʱ��1,ʱ��2,����;ʱ��3,ʱ��4,����;
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
					t[2] = status;  //�滻��״̬
					/**
					 * t��ʱ��1,ʱ��2,����״̬(���ն��� 4����Ԥ�� 5)
					 */
					list.add(t);
				}
			}
		}
		
		FangTai fangTai = new FangTai(conn);
		Calendar cal0 = Calendar.getInstance();
		/**
		 * ֮ǰ������
		 */
		cal0.add(Calendar.DAY_OF_MONTH, -lastInterval);
		String beginDate = StringTools.getDateByFormat(cal0.getTime(), "yyyy-MM-dd");
		cal0.add(Calendar.DAY_OF_MONTH, interval + lastInterval);
		String endDate = StringTools.getDateByFormat(cal0.getTime(), "yyyy-MM-dd");
		
		/**
		 * ÿһ�е�����
		 */
		StringBuffer lineDataBuf = new StringBuffer("var priceArray = new Array();\r\n");
		
		/**
		 * ����ÿ�����ݵļ۸�
		    var priceArray = new Array();
		    priceArray[0] = [[100,1],[110,0],[120,0],[121,0],[122,2],[133,0],[134,0],[135,0],[136,0]];   //һ�д���һ�����ݵ����ݣ��磺ÿҳ12��
		    priceArray[1] = [[200,0],[210,0],[220,2],[221,0],[222,0]];
		    priceArray[2] = [[300,0],[310,1],[320,0],[321,0],[322,0],[333,0],[334,0],[335,2],[336,0]];
		**/
		int totalDays = interval + lastInterval;
		for(int i=0; i<lr.length(); i++)
		{
			ListObjectBean line = lr.get(i);
			
			/**
			 * ����ĳһ�����ݵ�guid
			 */
			String houseGuid = line.get(guidIndex).getOriginalValue();
			HashMap priceMap = fangTai.getDayOfPrice(houseGuid, beginDate, endDate);
			Calendar cal = Calendar.getInstance();
			/**
			 * ��ǰ�ƶ�ָ��������
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
				{//�����м۸�
					//���ص�ǰ���ݵĶ���
					ArrayList list = (ArrayList)ordersMap.get(houseGuid);
					if(list == null)
					{//û�з��������Ķ���
					    lineDataBuf.append("[\"").append(fto.getPrice()).append("\", 0, 0]");
					}
					else
					{
						String status = getHousetByOrderStatus(list, currentDate);
						if(status == null) status = "0";  //�������ڱ���
						lineDataBuf.append("[\"").append(fto.getPrice()).append("\", ").append(status).append(", 0]");
					}
				}
				else
				{//�޼۸�
					lineDataBuf.append("[\"\", 9, 0]");   //��Ч���޼۸�
				}
			}
			lineDataBuf.append("];\r\n");
		}		
		context.put("dateStr", lineDataBuf.insert(0, getDateString()).insert(0, allGuids.toString()).toString());
		
		
		/**
		 * ���������
		 */
		new CommonSelect().addOption(context);
		
		
		/**
		 * ͳ�Ƶ���Ķ�������
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
	 * �ж�ָ����ʱ���Ƿ��ж������Լ�������״̬
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
			 * �ַ����Ƚϣ�currentDate>temp[0]  currentDate<=temp[1]
			 *   ���   0
			 *   С��  -1
			 *   ����  1
			 */
			if(temp[0].compareTo(currentDate)<=0 && temp[1].compareTo(currentDate)>=0)
			{
				/**
				 * �����js��һ����temp[2]�洢����״̬
				 * 4 ���ն��� 
				 * 5 ��Ԥ�� 
				 */
				status = temp[2];
				
				return status;
			}
		}
		
		return status;
	}
	
	/**
	 * ���ؽ��쵽ָ�����ʱ�������
	 * @param interval
	 * @return
	 */
	private String getDateString() throws Exception
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -lastInterval);
		StringBuffer dateBuf = new StringBuffer();
		
		dateBuf.append("[\"").append(cal.get(Calendar.MONTH)+1).append("��").append(cal.get(Calendar.DAY_OF_MONTH)).append("��");
		dateBuf.append("\",\"����").append(weekName[cal.get(Calendar.DAY_OF_WEEK)]).append("\",\"")
		       .append(StringTools.getDateByFormat(cal.getTime(), "yyyy-MM-dd")).append("\"]");
		for(int i=1; i<this.interval + this.lastInterval; i++)
		{
			
			cal.add(Calendar.DAY_OF_MONTH, 1);
			dateBuf.append(",[\"").append(cal.get(Calendar.MONTH)+1).append("��").append(cal.get(Calendar.DAY_OF_MONTH)).append("��");
			dateBuf.append("\",\"����").append(weekName[cal.get(Calendar.DAY_OF_WEEK)]).append("\",\"")
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
