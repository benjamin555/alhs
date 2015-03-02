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
 * 查询当日房态
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
		 * 当天的日期
		 */
		String today = StringTools.getDateByFormat(new Date(), "yyyy-MM-dd");

		/**
		 * 选择查询的日期
		 */
		String selectedDate = context.getReqParameter("begindate");

		if(selectedDate==null)
		{
			selectedDate = today;
		}

		int guidIndex = context.getListObjectPara().getListObject().getListFieldByName("主表Guid").getIndex();       //订单GUID索引
		int i_qrzt = context.getListObjectPara().getListObject().getListFieldByName("订单确认状态").getIndex();        //订单确认状态索引
		int i_gzzt = context.getListObjectPara().getListObject().getListFieldByName("订单状态").getIndex();           //订单跟踪状态索引
		int i_rzrq = context.getListObjectPara().getListObject().getListFieldByName("入住日期").getIndex();           //入住日期索引
		int i_tfrq = context.getListObjectPara().getListObject().getListFieldByName("退房日期").getIndex();           //退房日期索引

		int i_xs = context.getListObjectPara().getListObject().getListFieldByName("自动取消时间_小时").getIndex();    
		int i_fz = context.getListObjectPara().getListObject().getListFieldByName("自动取消时间_分钟").getIndex();          

		if(lr != null && lr.length() > 0){
			for(int i = 0; i < lr.length(); i++){
				ListObjectBean lob = lr.get(i);
				String status = "";
				String ddqrzt = lob.get(i_qrzt).getOriginalValue();              //订单确认状态
				String ddgzzt = lob.get(i_gzzt).getOriginalValue();              //订单跟踪状态
				String rzrq = lob.get(i_rzrq).getOriginalValue();                //入住日期
				String tfrq = lob.get(i_tfrq).getOriginalValue();                //退房日期


				/**
				 * 锁定，预定
				 */
				if(ddqrzt.equals("6") || ddqrzt.equals("5")){	
					//当天的日期与订单区间的比较
					int todayCompare = beforeMiddleOrAfter(rzrq, tfrq, today);

					//查询的日期与订单区间的比较
					int selectedDateCompare = beforeMiddleOrAfter(rzrq, tfrq, selectedDate);

					//当前日等于入住日，当前日小于入住日
					if(todayCompare==0 || todayCompare==-1){
						//入住日
						if(selectedDateCompare==0){
							status = "入住日";
						}
						//中间
						else if(selectedDateCompare==1){
							status = "未入住";
						}
						//退房日
						else if(selectedDateCompare==2){
							status = "待退房";
						}
					}
					//其他情况自动转为可定了
				}
				/**
				 * 已订
				 */
				else if(ddqrzt.equals("4")){
					status = selectedDateBeforeOrAfter(rzrq, tfrq, selectedDate,today,ddgzzt);
				}

				/**
				 * 设置状态
				 */
				lob.get(guidIndex).setListFieldValue(status);

				
				/**
				 * 状态下拉
				 */
				map.put(status, status);
				

				/**
				 * 截止时间显示
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

		//当天的日期与订单区间的比较
		int todayCompare = beforeMiddleOrAfter(begindate, enddate, today);

		//查询的日期与订单区间的比较
		int selectedDateCompare = beforeMiddleOrAfter(begindate, enddate, selectedDate);

		//来否
		boolean isCome = false;
		if(ddgzzt.equals("2") || ddgzzt.equals("3") || ddgzzt.equals("4")){
			isCome = true;
		}

		//是否退房
		boolean isTuiFang = false;
		if(ddgzzt.equals("3") || ddgzzt.equals("4")){
			isTuiFang = true;
		}

		//当前日小于入住日(不存在入住情况)
		if(todayCompare==-1){
			if(selectedDateCompare==0){
				status="入住日";
			}
			else if(selectedDateCompare==1){
				status="未入住";
			}
			else if(selectedDateCompare==2){
				status="待退房";
			}

		}
		//当前日等于入住日
		if(todayCompare==0){
			//入住日
			if(selectedDateCompare==0){
				if(isCome){
					status="已入住";
				}
				else{
					status="入住日";
				}
			}
			//中间
			else if(selectedDateCompare==1){
				if(isCome){
					status="待住";
				}
				else{
					status="未入住";
				}
			}
			//退房日
			else if(selectedDateCompare==2){
				if(isCome){
					status="退房日";
				}
				else{
					status="待退房";
				}
			}
		}
		//当前日大于入住日（在住）
		else if(todayCompare==1){
			//入住日
			if(selectedDateCompare==0){
				if(isCome){
					status="已入住";
				}
				else{
					status="NO SHOW";
				}
			}
			//中间
			else if(selectedDate.compareTo(begindate)>0 && selectedDate.compareTo(enddate)<0){
				if(isCome){
					status="在住";
				}
				else{
					status="无信息";
				}
			}
			//最后一天
			else if(selectedDate.compareTo(enddate)==0){
				if(isCome){
                    //住2天 不存在待住
					if(begindate.compareTo(Common.getSpecifiedDay(selectedDate, -1))==0){
						status="在住";
					}
					else{
						status="待住";
					}
				}
				else{
					status="无信息";
				}
			}
			//退房日
			else if(selectedDateCompare==2){
				if(isCome){
					status="退房日";
				}
				else{
					status="无信息";
				}
			}
		}
		//当前日大于入住日（退房日）
		else if(todayCompare==2){
			//入住日
			if(selectedDateCompare==0){
				if(isCome){
					status="已入住";
				}
				else{
					status="NO SHOW";
				}
			}
			//中间
			else if(selectedDateCompare==1){
				if(isCome){
					status="已住";
				}
				else{
					status="无信息";
				}
			}
			//退房日
			else if(selectedDateCompare==2){
				if(isCome){
					if(isTuiFang){
						status="已退房";
					}
					else{
						status="退房日";
					}

				}
				else{
					status="无信息";
				}
			}
		}
		//当前日大于入住日（订单已过）
		else if(todayCompare==3){
			//入住日
			if(selectedDateCompare==0){
				if(isCome){
					status="已入住";
				}
				else{
					status="NO SHOW";
				}
			}
			//中间
			else if(selectedDateCompare==1){
				if(isCome){
					status="已住";
				}
				else{
					status="无信息";
				}
			}
			//退房日
			else if(selectedDateCompare==2){
				if(isCome){
					status="已退房";
				}
				else{
					status="无信息";
				}
			}
		}

		return status;

	}

	/**
	 * 判断所当前时间在订单时间的区间
	 * @param begindate
	 * @param enddate
	 * @param today
	 * @return
	 * @throws Exception
	 */
	private int beforeMiddleOrAfter(String begindate,String enddate,String today) throws Exception{
		int str=-2;
		//1.之前
		if(today.compareTo(begindate)<0){
			str = -1;
		}
		//2.入住日
		if(today.compareTo(begindate)==0){
			str = 0;
		}
		//3.中间
		else if(today.compareTo(begindate)>0 && today.compareTo(enddate)<=0){
			str = 1;

		}
		//4.退房日
		else if(today.compareTo(Common.getSpecifiedDay(enddate,1))==0){
			str = 2;
		}
		//5.订单已过
		else if(today.compareTo(Common.getSpecifiedDay(enddate,1))>=0){
			str = 3;
		}

		return str;
	}

	public String getErrorMessage() {
		return null;
	}
}
