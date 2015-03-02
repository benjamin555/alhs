package net.alhs.template.action.order;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.alhs.api.Common;
import net.business.engine.ListObjectBean;
import net.business.engine.ListResult;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.sysmain.util.StringTools;
/**
 * ��ѯ���շ�̬
 * @author Administrator
 *
 */
public class SearchTodayFangTai implements I_TemplateAction{
	public int execute(TemplateContext context) throws Exception {
		ListResult lr = context.getListResult();
		//Connection conn = context.getConn();
		Map map =new HashMap();
		StringBuffer sb = new StringBuffer();
		sb.append("<select class=style_box onchange='changefangtai(this);' name=drft id=drft><option></option>");
		
		/**
		 * ���������
		 */
		String today = StringTools.getDateByFormat(new Date(), "yyyy-MM-dd");

		/**
		 * ѡ���ѯ������
		 */
		String selectedDate = context.getReqParameter("begindate");

		if(selectedDate==null)
		{
			selectedDate = today;
		}

		int guidIndex = context.getListObjectPara().getListObject().getListFieldByName("����Guid").getIndex();       //����GUID����
		int i_qrzt = context.getListObjectPara().getListObject().getListFieldByName("����ȷ��״̬").getIndex();        //����ȷ��״̬����
		int i_gzzt = context.getListObjectPara().getListObject().getListFieldByName("����״̬").getIndex();           //��������״̬����
		int i_rzrq = context.getListObjectPara().getListObject().getListFieldByName("��ס����").getIndex();           //��ס��������
		int i_tfrq = context.getListObjectPara().getListObject().getListFieldByName("�˷�����").getIndex();           //�˷���������

		int i_xs = context.getListObjectPara().getListObject().getListFieldByName("�Զ�ȡ��ʱ��_Сʱ").getIndex();    
		int i_fz = context.getListObjectPara().getListObject().getListFieldByName("�Զ�ȡ��ʱ��_����").getIndex();          

		if(lr != null && lr.length() > 0){
			for(int i = 0; i < lr.length(); i++){
				ListObjectBean lob = lr.get(i);
				String status = "";
				String ddqrzt = lob.get(i_qrzt).getOriginalValue();              //����ȷ��״̬
				String ddgzzt = lob.get(i_gzzt).getOriginalValue();              //��������״̬
				String rzrq = lob.get(i_rzrq).getOriginalValue();                //��ס����
				String tfrq = lob.get(i_tfrq).getOriginalValue();                //�˷�����


				/**
				 * ������Ԥ��
				 */
				if(ddqrzt.equals("6") || ddqrzt.equals("5")){	
					//����������붩������ıȽ�
					int todayCompare = beforeMiddleOrAfter(rzrq, tfrq, today);

					//��ѯ�������붩������ıȽ�
					int selectedDateCompare = beforeMiddleOrAfter(rzrq, tfrq, selectedDate);

					//��ǰ�յ�����ס�գ���ǰ��С����ס��
					if(todayCompare==0 || todayCompare==-1){
						//��ס��
						if(selectedDateCompare==0){
							status = "��ס��";
						}
						//�м�
						else if(selectedDateCompare==1){
							status = "δ��ס";
						}
						//�˷���
						else if(selectedDateCompare==2){
							status = "���˷�";
						}
					}
					//��������Զ�תΪ�ɶ���
				}
				/**
				 * �Ѷ�
				 */
				else if(ddqrzt.equals("4")){
					status = selectedDateBeforeOrAfter(rzrq, tfrq, selectedDate,today,ddgzzt);
				}

				/**
				 * ����״̬
				 */
				lob.get(guidIndex).setListFieldValue(status);

				
				/**
				 * ״̬����
				 */
				map.put(status, status);
				

				/**
				 * ��ֹʱ����ʾ
				 */
				if(rzrq.equals(selectedDate)){
					String xs = lob.get(i_xs).getListFieldValue();
					String fz = lob.get(i_fz).getListFieldValue();
					lob.get(i_xs).setListFieldValue(xs+(fz.equals("")?"":(":"+fz)));
				}
				else{
					lob.get(i_xs).setListFieldValue("");
				}


			}
		}
		
		Iterator it = map.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry pairs = (Map.Entry)it.next();
			String key = (String)pairs.getKey();
			sb.append("<option value='").append(key).append("'>").append(key).append("</option>");
		}
		context.put("fangtai", sb.append("</select>").toString());
		
		return SUCCESS;
	}
	/**
	 * 
	 * @param begindate
	 * @param enddate
	 * @param selectedDate
	 * @param today
	 * @param ddgzzt
	 * @return
	 * @throws Exception
	 */
	private String selectedDateBeforeOrAfter(String begindate,String enddate,String selectedDate,String today,String ddgzzt)throws Exception{
		String status = "";

		//����������붩������ıȽ�
		int todayCompare = beforeMiddleOrAfter(begindate, enddate, today);

		//��ѯ�������붩������ıȽ�
		int selectedDateCompare = beforeMiddleOrAfter(begindate, enddate, selectedDate);

		//����
		boolean isCome = false;
		if(ddgzzt.equals("2") || ddgzzt.equals("3") || ddgzzt.equals("4")){
			isCome = true;
		}

		//�Ƿ��˷�
		boolean isTuiFang = false;
		if(ddgzzt.equals("3") || ddgzzt.equals("4")){
			isTuiFang = true;
		}

		//��ǰ��С����ס��(��������ס���)
		if(todayCompare==-1){
			if(selectedDateCompare==0){
				status="��ס��";
			}
			else if(selectedDateCompare==1){
				status="δ��ס";
			}
			else if(selectedDateCompare==2){
				status="���˷�";
			}

		}
		//��ǰ�յ�����ס��
		if(todayCompare==0){
			//��ס��
			if(selectedDateCompare==0){
				if(isCome){
					status="����ס";
				}
				else{
					status="��ס��";
				}
			}
			//�м�
			else if(selectedDateCompare==1){
				if(isCome){
					status="��ס";
				}
				else{
					status="δ��ס";
				}
			}
			//�˷���
			else if(selectedDateCompare==2){
				if(isCome){
					status="�˷���";
				}
				else{
					status="���˷�";
				}
			}
		}
		//��ǰ�մ�����ס�գ���ס��
		else if(todayCompare==1){
			//��ס��
			if(selectedDateCompare==0){
				if(isCome){
					status="����ס";
				}
				else{
					status="NO SHOW";
				}
			}
			//�м�
			else if(selectedDate.compareTo(begindate)>0 && selectedDate.compareTo(enddate)<0){
				if(isCome){
					status="��ס";
				}
				else{
					status="����Ϣ";
				}
			}
			//���һ��
			else if(selectedDate.compareTo(enddate)==0){
				if(isCome){
                    //ס2�� �����ڴ�ס
					if(begindate.compareTo(Common.getSpecifiedDay(selectedDate, -1))==0){
						status="��ס";
					}
					else{
						status="��ס";
					}
				}
				else{
					status="����Ϣ";
				}
			}
			//�˷���
			else if(selectedDateCompare==2){
				if(isCome){
					status="�˷���";
				}
				else{
					status="����Ϣ";
				}
			}
		}
		//��ǰ�մ�����ס�գ��˷��գ�
		else if(todayCompare==2){
			//��ס��
			if(selectedDateCompare==0){
				if(isCome){
					status="����ס";
				}
				else{
					status="NO SHOW";
				}
			}
			//�м�
			else if(selectedDateCompare==1){
				if(isCome){
					status="��ס";
				}
				else{
					status="����Ϣ";
				}
			}
			//�˷���
			else if(selectedDateCompare==2){
				if(isCome){
					if(isTuiFang){
						status="���˷�";
					}
					else{
						status="�˷���";
					}

				}
				else{
					status="����Ϣ";
				}
			}
		}
		//��ǰ�մ�����ס�գ������ѹ���
		else if(todayCompare==3){
			//��ס��
			if(selectedDateCompare==0){
				if(isCome){
					status="����ס";
				}
				else{
					status="NO SHOW";
				}
			}
			//�м�
			else if(selectedDateCompare==1){
				if(isCome){
					status="��ס";
				}
				else{
					status="����Ϣ";
				}
			}
			//�˷���
			else if(selectedDateCompare==2){
				if(isCome){
					status="���˷�";
				}
				else{
					status="����Ϣ";
				}
			}
		}

		return status;

	}

	/**
	 * �ж�����ǰʱ���ڶ���ʱ�������
	 * @param begindate
	 * @param enddate
	 * @param today
	 * @return
	 * @throws Exception
	 */
	private int beforeMiddleOrAfter(String begindate,String enddate,String today) throws Exception{
		int str=-2;
		//1.֮ǰ
		if(today.compareTo(begindate)<0){
			str = -1;
		}
		//2.��ס��
		if(today.compareTo(begindate)==0){
			str = 0;
		}
		//3.�м�
		else if(today.compareTo(begindate)>0 && today.compareTo(enddate)<=0){
			str = 1;

		}
		//4.�˷���
		else if(today.compareTo(Common.getSpecifiedDay(enddate,1))==0){
			str = 2;
		}
		//5.�����ѹ�
		else if(today.compareTo(Common.getSpecifiedDay(enddate,1))>=0){
			str = 3;
		}

		return str;
	}

	public String getErrorMessage() {
		return null;
	}
}
