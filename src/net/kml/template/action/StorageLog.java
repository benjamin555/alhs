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


		//新增
		if(context.getTemplatePara().getEditType() == I_TemplateConstant.ACTION_TYPE_ADD)
		{
			for (int i = 0; i < cwxxGuid.length; i++) {
				doInsert(context,wlGuid,ck[i]+hj[i]+mian[i]+ceng[i]+ge[i],"添加","");
			}
			return SUCCESS;
		}
		else{
			Map beginMap = new HashMap(0);
			Map afterMap = new HashMap(0);
			PreparedStatement ps = conn.prepareStatement("select guid,ck,hj,mian,ceng,ge from cwxx where wlGuid=?");
			ps.setString(1, wlGuid);
			ResultSet rs = ps.executeQuery();

			//操作前集合（key:guid/value:仓位）
			while(rs.next()){
				String guid = rs.getString(1);      //仓位Guid
				String cwName = rs.getString(2)+rs.getString(3)+rs.getString(4)+rs.getString(5)+rs.getString(6);    //修改前仓位
				beginMap.put(guid, cwName);
			}

			//操作后集合（key:guid/value:仓位）
			if(cwxxGuid!=null && cwxxGuid.length>0){
				for (int i = 0; i < cwxxGuid.length; i++) {
					String name = ck[i]+hj[i]+mian[i]+ceng[i]+ge[i];  //修改后仓位
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
						//System.out.println("修改："+value);
						doInsert(context,wlGuid,value,"迁移","仓位：["+value+"] 迁移到 ["+afterMap.get(key)+"]");
					}
				}
				else{
					doInsert(context,wlGuid,value,"删除","");
					//System.out.println("删除："+value);
				}
				//System.out.println(pairs.getKey() + " = " + pairs.getValue());
			}

			Iterator ito = afterMap.entrySet().iterator();
			while (ito.hasNext())
			{
				Map.Entry obj = (Map.Entry)ito.next();
				//System.out.println(obj.getKey()+"/"+obj.getValue());
				if(!beginMap.containsKey(obj.getKey())){
					//System.out.println("添加："+obj.getValue());
					doInsert(context,wlGuid,obj.getValue().toString(),"添加","");
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
