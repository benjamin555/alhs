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
 * �ͻ�����ȷ���ύ��ִ��
 * @author Administrator
 *
 */
public class CustomerConfirm implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		Operator op = Authentication.getUserFromSession(context.getRequest()); // ��װ�ػ�����
		Connection conn = context.getConn();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String ddGuid = context.getReqParameter("guid");             //����GUID


		/**
		 * ��������
		 */
		String [] rdrq = context.getReqParameterValues("rdrq");
		String [] ldrq = context.getReqParameterValues("ldrq");
		String [] _rzts = context.getReqParameterValues("ts");

		/**
		 * ÿ�շ�����Ϣ
		 */
		String [] rq = context.getReqParameterValues("rq");
		String [] yfj = context.getReqParameterValues("yfj");
		String [] qrfj = context.getReqParameterValues("qrfj");
		//String [] bz = context.getReqParameterValues("bz");

		/**
		 * �ͻ���Ϣ
		 */
		//String xm = context.getReqParameter("ddzb_xm");
		//String lxdh = context.getReqParameter("ddzb_lxdh");
		//String sfz = context.getReqParameter("ddzb_sfz");
		TableField xm = context.getTableFieldByAlias("ddzb.xm");    
		TableField sfz = context.getTableFieldByAlias("ddzb.sfz"); 
		TableField lxdh = context.getTableFieldByAlias("ddzb.lxdh");

		/**
		 * ���������е���꣬������ڣ�ȡ��һ�������������ڣ����һ�������������ڣ�
		 */
		String begindate = rdrq[0];
		String enddate = ldrq[ldrq.length-1];

		String sql = "insert into ddfjxx(guid,zbGuid,rq,yfj,qrfj,bz,ts)values(?,?,?,?,?,?,?)";
		ps = conn.prepareStatement(sql);

		double ffyj = 0.0;      //������ԭ��
		double zje = 0.0;       //ȷ���ܷ���

		if(rq!=null && rq.length>0){
			for (int i = 0; i < rq.length; i++) {
				double _yfj = Double.parseDouble(yfj[i]);  //ԭ����
				double fj = Double.parseDouble(qrfj[i]);   //ȷ�Ϸ���
				int ts = rq[i].split(",").length;          //����
				ps.setString(1, new GUID().toString());
				ps.setString(2, ddGuid);
				ps.setString(3, rq[i]);
				ps.setDouble(4, Double.parseDouble(yfj[i]));
				ps.setDouble(5, fj);
				//ps.setString(6, bz[i]);
				ps.setString(6, "");
				ps.setInt(7, ts);

				zje+=(fj*ts);     //�ۼƽ��(ȷ�Ϸ���*����)
				ffyj+=(_yfj*ts);  //����ԭ��(ԭ����*����)

				ps.addBatch();
			}
		}
		ps.executeBatch();

		StringBuffer sb = new StringBuffer();
		int total = 0;                          //������


		/**
		 * �����סʱ������
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

				//��סʱ�������ַ�������ʽ��ʱ��1��ʱ��2����������
				sb.append(rdrq[i]).append(",").append(ldrq[i]).append(",").append(_rzts[i]);
				if(i!=rdrq.length){
					sb.append(";");
				}
				total+=Integer.parseInt(_rzts[i]);

			}
			ps.executeBatch();
		}

		/**
		 * ͬ����վ����
		 */
		net.alhs.api.CalendarToWebThread.addOrder(ddGuid);


		/**
		 * ����ִ�У�֮��ִ�У�ֱ��return
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
			 * ����ס����,����֮�����¼��㶩����ط���
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
		 * Ĭ�����һ����ס����Ϣ���½�����ִ�У�
		 */
		if(xm!=null && sfz!=null && lxdh!=null){
			sql = "insert into ddrzr(guid,zbGuid,xm,lxdh,sfz,zjlx)values(?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, new GUID().toString());
			ps.setString(2, ddGuid);
			ps.setString(3, xm.getFieldValue());
			ps.setString(4, lxdh.getFieldValue());
			ps.setString(5, sfz.getFieldValue());
			ps.setString(6, "���֤");
			ps.executeUpdate();
		}

		UpFile up = context.getFileUpload().getFiles().getUpFile("c_447_97_ddsk_fj"); //����

		String _ysk = context.getReqParameterValues("c_447_97_ddsk_skje")[0];         //���տ�
		double ysk = Double.parseDouble((_ysk==null ||_ysk.trim().equals(""))?"0":_ysk);
		
		/**
		 * �޸ķ����ܼƣ��ܽ����տ���,��סʱ�����䣬��ס����,������
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
		 * �����ֽ�ͳ��
		 */
		String sklx = context.getReqParameterValues("c_447_97_ddsk_sklx")[0];
		if(sklx.equals("�ֽ�") && ysk>0){
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
		 * ɾ�����տ��¼
		 */
		if(sklx.equals("") && ysk==0 && up==null){
			sql = "delete from fwddsk where zbGuid='"+ddGuid+"'";
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
		}
		
		/**
		 * ���Ͷ���
		 */
		String ddMsg = context.getReqParameter("ddMsg");                 //�Ƿ��Ͷ�������
		String addressMsg = context.getReqParameter("addressMsg");       //�Ƿ��͵�ַ����

		if(ddMsg!=null && ddMsg.equals("1")){
			MsgInfo.sendOrderMsg(ddGuid);
		}
		if(addressMsg!=null && addressMsg.equals("1")){
			MsgInfo.sendOrderAddressMsg(ddGuid);
		}
		
		//��ת
		setJs(context.getRequest());

		return SUCCESS;
	}

	/**
	 * ˢ��
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
