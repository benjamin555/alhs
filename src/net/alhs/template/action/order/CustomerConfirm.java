package net.alhs.template.action.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import net.alhs.api.Common;
import net.alhs.common.MsgInfo;
import net.business.engine.TableField;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.sysmain.common.I_CommonConstant;
import net.sysmain.common.upload.UpFile;
import net.sysmain.util.GUID;
import net.sysplat.access.Authentication;
import net.sysplat.common.Operator;
/**
 * 客户订房确认提交后执行
 * @author Administrator
 *
 */
public class CustomerConfirm implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		Operator op = Authentication.getUserFromSession(context.getRequest()); // 封装回话对象
		Connection conn = context.getConn();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String ddGuid = context.getReqParameter("guid");             //订单GUID


		/**
		 * 日期区间
		 */
		String [] rdrq = context.getReqParameterValues("rdrq");
		String [] ldrq = context.getReqParameterValues("ldrq");
		String [] _rzts = context.getReqParameterValues("ts");

		/**
		 * 每日房间信息
		 */
		String [] rq = context.getReqParameterValues("rq");
		String [] yfj = context.getReqParameterValues("yfj");
		String [] qrfj = context.getReqParameterValues("qrfj");
		//String [] bz = context.getReqParameterValues("bz");

		/**
		 * 客户信息
		 */
		//String xm = context.getReqParameter("ddzb_xm");
		//String lxdh = context.getReqParameter("ddzb_lxdh");
		//String sfz = context.getReqParameter("ddzb_sfz");
		TableField xm = context.getTableFieldByAlias("ddzb.xm");    
		TableField sfz = context.getTableFieldByAlias("ddzb.sfz"); 
		TableField lxdh = context.getTableFieldByAlias("ddzb.lxdh");

		/**
		 * 订单主表中的入店，离店日期（取第一个区间的入店日期，最后一个区间的离店日期）
		 */
		String begindate = rdrq[0];
		String enddate = ldrq[ldrq.length-1];

		String sql = "insert into ddfjxx(guid,zbGuid,rq,yfj,qrfj,bz,ts)values(?,?,?,?,?,?,?)";
		ps = conn.prepareStatement(sql);

		double ffyj = 0.0;      //房费总原价
		double zje = 0.0;       //确认总房价

		if(rq!=null && rq.length>0){
			for (int i = 0; i < rq.length; i++) {
				double _yfj = Double.parseDouble(yfj[i]);  //原房价
				double fj = Double.parseDouble(qrfj[i]);   //确认房价
				int ts = rq[i].split(",").length;          //天数
				ps.setString(1, new GUID().toString());
				ps.setString(2, ddGuid);
				ps.setString(3, rq[i]);
				ps.setDouble(4, Double.parseDouble(yfj[i]));
				ps.setDouble(5, fj);
				//ps.setString(6, bz[i]);
				ps.setString(6, "");
				ps.setInt(7, ts);

				zje+=(fj*ts);     //累计金额(确认房价*天数)
				ffyj+=(_yfj*ts);  //房费原价(原房价*天数)

				ps.addBatch();
			}
		}
		ps.executeBatch();

		StringBuffer sb = new StringBuffer();
		int total = 0;                          //总天数


		/**
		 * 添加入住时间区间
		 */
		sql = "insert into ddrzqj(guid,zbGuid,rzrq,tfrq) values(?,?,?,?)";
		ps = conn.prepareStatement(sql);

		if(rdrq!=null && rdrq.length>0){
			for (int i = 0; i < rdrq.length; i++) {
				ps.setString(1, new GUID().toString());
				ps.setString(2, ddGuid);
				ps.setString(3, rdrq[i]);
				ps.setString(4, ldrq[i]);
				ps.addBatch();

				//入住时间区间字符串（格式：时间1，时间2，天数；）
				sb.append(rdrq[i]).append(",").append(ldrq[i]).append(",").append(_rzts[i]);
				if(i!=rdrq.length){
					sb.append(";");
				}
				total+=Integer.parseInt(_rzts[i]);

			}
			ps.executeBatch();
		}

		/**
		 * 同步网站数据
		 */
		net.alhs.api.CalendarToWebThread.addOrder(ddGuid);


		/**
		 * 换房执行，之后不执行，直接return
		 */
		if(xm==null && sfz==null && lxdh==null){
			sql = "update fwddzb set ffxj=?,zje=ifnull(qtfy,0)+?,yk=ifnull(ysk,0)-(ifnull(qtfy,0)+?),ffyj=?,jff=?,rzsjqj=?,rzrq=?,tfrq=?,rzts=?,zcqx=null where guid=?";
			ps = conn.prepareStatement(sql);
			ps.setDouble(1, zje);
			ps.setDouble(2, zje);
			ps.setDouble(3, zje);
			ps.setDouble(4, ffyj);
			ps.setDouble(5, zje);
			ps.setString(6, sb.toString());
			ps.setString(7, begindate);
			ps.setString(8, Common.getSpecifiedDay(enddate,1));
			ps.setInt(9, total);
			ps.setString(10, ddGuid);
			ps.executeUpdate();
			
			/**
			 * 已入住订单,换房之后，重新计算订单相关费用
			 */
			sql = "select ddzt from fwddzb where guid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, ddGuid);
			rs = ps.executeQuery();
			if(rs.next() && rs.getString(1).equals("2")){
				OrderFreeAction.initOrderFree(ddGuid);
			}
			
			return SUCCESS;
		}


		/**
		 * 默认添加一条入住人信息（新建订单执行）
		 */
		if(xm!=null && sfz!=null && lxdh!=null){
			sql = "insert into ddrzr(guid,zbGuid,xm,lxdh,sfz,zjlx)values(?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, new GUID().toString());
			ps.setString(2, ddGuid);
			ps.setString(3, xm.getFieldValue());
			ps.setString(4, lxdh.getFieldValue());
			ps.setString(5, sfz.getFieldValue());
			ps.setString(6, "身份证");
			ps.executeUpdate();
		}

		UpFile up = context.getFileUpload().getFiles().getUpFile("c_447_97_ddsk_fj"); //附件

		String _ysk = context.getReqParameterValues("c_447_97_ddsk_skje")[0];         //已收款
		double ysk = Double.parseDouble((_ysk==null ||_ysk.trim().equals(""))?"0":_ysk);
		
		/**
		 * 修改房费总计，总金额，已收款，余款,入住时间区间，入住天数,净房费
		 */
		sql = "update fwddzb set ffxj=?,zje=?,ysk=?,yk=?,rzsjqj=?,rzrq=?,tfrq=?,rzts=?,jff=?,ffyj=? where guid=?";
		ps = conn.prepareStatement(sql);
		ps.setDouble(1, zje);
		ps.setDouble(2, zje);
		ps.setDouble(3, ysk);
		ps.setDouble(4, ysk-zje);
		ps.setString(5, sb.toString());
		ps.setString(6, begindate);
		ps.setString(7, Common.getSpecifiedDay(enddate,1));
		ps.setInt(8, total);
		ps.setDouble(9, zje);
		ps.setDouble(10, ffyj);
		ps.setString(11, ddGuid);
		ps.executeUpdate();

		/**
		 * 个人现金统计
		 */
		String sklx = context.getReqParameterValues("c_447_97_ddsk_sklx")[0];
		if(sklx.equals("现金") && ysk>0){
			PersonalCashBean pcb = new PersonalCashBean();
			pcb.setXm(op.getName());
			pcb.setXmid(op.getLoginId());
			pcb.setBm((String)op.getAttribute("jiudian"));
			pcb.setBmid(op.getOrganizeId()+"");
			pcb.setMoney(ysk);

			PersonalCashStatistics pc = new PersonalCashStatistics();
			pc.initMoney(pcb);
		}

		/**
		 * 删除空收款记录
		 */
		if(sklx.equals("") && ysk==0 && up==null){
			sql = "delete from fwddsk where zbGuid='"+ddGuid+"'";
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
		}
		
		/**
		 * 发送短信
		 */
		String ddMsg = context.getReqParameter("ddMsg");                 //是否发送订单短信
		String addressMsg = context.getReqParameter("addressMsg");       //是否发送地址短信

		if(ddMsg!=null && ddMsg.equals("1")){
			MsgInfo.sendOrderMsg(ddGuid);
		}
		if(addressMsg!=null && addressMsg.equals("1")){
			MsgInfo.sendOrderAddressMsg(ddGuid);
		}
		
		//跳转
		setJs(context.getRequest());

		return SUCCESS;
	}

	/**
	 * 刷新
	 * @param request
	 */
	private void setJs(HttpServletRequest request){
		String js = "top.window.dialogArguments.frmQuery_c_989_832.submit();top.window.close();";
		request.setAttribute(I_CommonConstant.JAVA_SCRIPT_CODE,js);
	}

	public String getErrorMessage() {
		return null;
	}
}
