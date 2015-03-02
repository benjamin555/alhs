package net.kml.template.action;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.sysmain.common.I_TemplateConstant;
import net.sysmain.util.GUID;
import net.sysplat.access.Authentication;
import net.sysplat.common.Operator;

public class StorageLog implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		HttpServletRequest request = context.getRequest();
		Connection conn = context.getConn();
		String wlGuid = request.getParameter("wlGuid");
		String [] cwxxGuid = request.getParameterValues("c_242_475_SeqField");
		String [] ck = request.getParameterValues("c_242_475_cwxx_ck");
		String [] hj = request.getParameterValues("c_242_475_cwxx_hj");
		String [] mian = request.getParameterValues("c_242_475_cwxx_mian");
		String [] ceng = request.getParameterValues("c_242_475_cwxx_ceng");
		String [] ge = request.getParameterValues("c_242_475_cwxx_ge");


		//����
		if(context.getTemplatePara().getEditType() == I_TemplateConstant.ACTION_TYPE_ADD)
		{
			for (int i = 0; i < cwxxGuid.length; i++) {
				doInsert(context,wlGuid,ck[i]+hj[i]+mian[i]+ceng[i]+ge[i],"���","");
			}
			return SUCCESS;
		}
		else{
			Map beginMap = new HashMap(0);
			Map afterMap = new HashMap(0);
			PreparedStatement ps = conn.prepareStatement("select guid,ck,hj,mian,ceng,ge from cwxx where wlGuid=?");
			ps.setString(1, wlGuid);
			ResultSet rs = ps.executeQuery();

			//����ǰ���ϣ�key:guid/value:��λ��
			while(rs.next()){
				String guid = rs.getString(1);      //��λGuid
				String cwName = rs.getString(2)+rs.getString(3)+rs.getString(4)+rs.getString(5)+rs.getString(6);    //�޸�ǰ��λ
				beginMap.put(guid, cwName);
			}

			//�����󼯺ϣ�key:guid/value:��λ��
			if(cwxxGuid!=null && cwxxGuid.length>0){
				for (int i = 0; i < cwxxGuid.length; i++) {
					String name = ck[i]+hj[i]+mian[i]+ceng[i]+ge[i];  //�޸ĺ��λ
					afterMap.put(cwxxGuid[i].equals("")?new GUID().toString():cwxxGuid[i],name);
				}
			}

			Iterator it = beginMap.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry pairs = (Map.Entry)it.next();
				String key = (String)pairs.getKey();
				String value = (String)pairs.getValue();
				if(afterMap.containsKey(pairs.getKey())){
					if(!value.equals(afterMap.get(key))){
						//System.out.println("�޸ģ�"+value);
						doInsert(context,wlGuid,value,"Ǩ��","��λ��["+value+"] Ǩ�Ƶ� ["+afterMap.get(key)+"]");
					}
				}
				else{
					doInsert(context,wlGuid,value,"ɾ��","");
					//System.out.println("ɾ����"+value);
				}
				//System.out.println(pairs.getKey() + " = " + pairs.getValue());
			}

			Iterator ito = afterMap.entrySet().iterator();
			while (ito.hasNext())
			{
				Map.Entry obj = (Map.Entry)ito.next();
				//System.out.println(obj.getKey()+"/"+obj.getValue());
				if(!beginMap.containsKey(obj.getKey())){
					//System.out.println("��ӣ�"+obj.getValue());
					doInsert(context,wlGuid,obj.getValue().toString(),"���","");
				}
			}
		}
		return SUCCESS;
	}
	public void doInsert(TemplateContext context,String wlGuid,String cwmc,String type,String remark) throws Exception{
		Operator operator = Authentication.getUserFromSession(context.getRequest());
		String sql = "insert into cwrz(guid,wlGuid,cwh,cgy,qdsj,cz,bz,type) values(?,?,?,?,now(),?,?,'1')";
		Connection conn = context.getConn();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, new GUID().toString());
		ps.setString(2, wlGuid);
		ps.setString(3, cwmc);
		ps.setString(4, operator.getName());
		ps.setString(5, type);
		ps.setString(6, remark);
		ps.executeUpdate();
	}

	public String getErrorMessage() {
		return null;
	}

}
