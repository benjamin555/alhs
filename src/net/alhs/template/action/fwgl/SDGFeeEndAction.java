package net.alhs.template.action.fwgl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.business.template.action.SerialNumberAction;
import net.sysmain.common.EngineTools;
import net.sysmain.util.GUID;

/**
 * ����ˮ���磬����ѣ��ύ���Զ������˺ŵ��ӵ�
 * @author Administrator
 *
 */
public class SDGFeeEndAction implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest()); // ��װ�ػ�����
		String guid = context.getReqParameter("guid");

		Connection conn = context.getConn();

		List listYhzh = new ArrayList();        //�����˺���Ϣ

		List listWebId = new ArrayList();       //�����˺���Ϣ

		Map mapYhzh = new HashMap();            //�����˺ŵ���

		Map mapWebId = new HashMap();           //�����˺ŵ���

		String sql = "SELECT fwzh.glfzhlx,fwzh.glfzhGuid,fwzh.glfyzzhid,ifnull(sqmx.glf,0) glf,fwzh.sfzhlx,fwzh.sfzhGuid,fwzh.sfyzzhid,ifnull(sqmx.sf,0) sf,fwzh.dfzhlx,fwzh.dfzhGuid,fwzh.dfyzzhid,ifnull(sqmx.df,0) df,fwzh.rqfzhlx,fwzh.rqfzhGuid,fwzh.rqfyzzhid,ifnull(sqmx.rqf,0) rqf FROM house fwzb,fwskzh fwzh,fy_sqmx sqmx WHERE sqmx.houseGuid=fwzb.guid AND fwzh.houseGuid=fwzb.guid AND sqmx.zbGuid=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, guid);
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			String glflx = rs.getString("glfzhlx");
			String sflx = rs.getString("sfzhlx");
			String dflx = rs.getString("dfzhlx");
			String rqflx = rs.getString("rqfzhlx");

			/**
			 * �����
			 */
			if(glflx.equals("1")){
				/**
				 * �����˺�ID
				 * ����
				 */
				listWebId.add(new String[]{rs.getString("glfyzzhid"),rs.getString("glf")});
			}
			else{
				/**
				 * �˺�Guid(�����˺ű�)
				 * ����
				 */
				listYhzh.add(new String[]{rs.getString("glfzhGuid"),rs.getString("glf")});
			}

			/**
			 * ˮ��
			 */
			if(sflx.equals("1")){
				/**
				 * �����˺�ID
				 * ����
				 */
				listWebId.add(new String[]{rs.getString("sfyzzhid"),rs.getString("sf")});
			}
			else{
				/**
				 * �˺�Guid(�����˺ű�)
				 * ����
				 */
				listYhzh.add(new String[]{rs.getString("sfzhGuid"),rs.getString("sf")});
			}

			/**
			 * ���
			 */
			if(dflx.equals("1")){
				/**
				 * �����˺�ID
				 * ����
				 */
				listWebId.add(new String[]{rs.getString("dfyzzhid"),rs.getString("df")});
			}
			else{
				/**
				 * �˺�Guid(�����˺ű�)
				 * ����
				 */
				listYhzh.add(new String[]{rs.getString("dfzhGuid"),rs.getString("df")});
			}

			/**
			 * ȼ����
			 */
			if(rqflx.equals("1")){
				/**
				 * �����˺�ID
				 * ����
				 */
				listWebId.add(new String[]{rs.getString("rqfyzzhid"),rs.getString("rqf")});
			}
			else{
				/**
				 * �˺�Guid(�����˺ű�)
				 * ����
				 */
				listYhzh.add(new String[]{rs.getString("rqfzhGuid"),rs.getString("rqf")});
			}
		}

		for (int i = 0; i < listWebId.size(); i++) {
			String [] zhIds = (String [])listWebId.get(i);
			String id = zhIds[0];                           //����ID
			double free = Double.parseDouble(zhIds[1]);     //����
			
			if(free<=0){
				continue;
			}
			
			if(mapWebId.containsKey(zhIds[0])){
				mapWebId.put(id, Double.parseDouble(mapWebId.get(id)+"")+free+"");
			}
			else{
				mapWebId.put(id, free+"");
			}
		}

		for (int i = 0; i < listYhzh.size(); i++) {
			String [] yhzh = (String [])listYhzh.get(i);
			String id = yhzh[0];                           //�����˺�GUID
			double free = Double.parseDouble(yhzh[1]);     //����
			
			if(free<=0){
				continue;
			}
			
			if(mapYhzh.containsKey(yhzh[0])){
				mapYhzh.put(id, Double.parseDouble(mapYhzh.get(id)+"")+free+"");
			}
			else{
				mapYhzh.put(id, free+"");
			}
		}

		/**
		 * �޸�
		 */
		if(context.getTemplatePara().getEditType() == 1){
			conn.createStatement().execute("DELETE FROM fy_zhdjmx where zbGuid='"+guid+"'");
		}
		else{
			/**
			 * �޸ĵ��ӵ���
			 */
			EngineTools etools = new EngineTools();
			//etools.setType(EngineTools.CALL_FOR_POST);

			sql = "update fy_sqzb set djdh=? where guid = ?";
			ps=conn.prepareStatement(sql);
			ps.setString(1, "DJD"+etools.dateSerial(conn, 43, 3));
			ps.setString(2, guid);
			ps.execute();
		}

		sql = "insert into fy_zhdjmx(guid,zbGuid,yhzhGuid,wlzhId,zje) values(?,?,?,?,?)";
		ps = conn.prepareStatement(sql);

		Iterator it = mapWebId.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry pairs = (Map.Entry)it.next();
			String key = (String)pairs.getKey();
			String value = (String)pairs.getValue();
			
			ps.setString(1, new GUID().toString());
			ps.setString(2, guid);
			ps.setString(3, "");
			ps.setString(4, key);
			ps.setDouble(5, Double.parseDouble(value));
			ps.addBatch();
		}
		Iterator it0 = mapYhzh.entrySet().iterator();
		while (it0.hasNext())
		{
			Map.Entry pairs = (Map.Entry)it0.next();
			String key = (String)pairs.getKey();
			String value = (String)pairs.getValue();
			
			ps.setString(1, new GUID().toString());
			ps.setString(2, guid);
			ps.setString(3, key);
			ps.setString(4, "");
			ps.setDouble(5, Double.parseDouble(value));
			ps.addBatch();
		}
		ps.executeBatch();

		return SUCCESS;
	}

	public String getErrorMessage() {
		return null;
	}
}
