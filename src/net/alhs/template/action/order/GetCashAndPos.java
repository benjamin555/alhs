package net.alhs.template.action.order;

import java.sql.ResultSet;

import net.alhs.common.CommonSelect;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;

/**
 * ͳ���ֽ�pos���ܽ��
 * @author Administrator
 *
 */
public class GetCashAndPos implements I_TemplateAction
{

	public int execute(TemplateContext context) throws Exception {

		new CommonSelect().execute(context);

		String sql = context.getListObjectPara().getQuerySql();
		
		ResultSet rs = context.getConn().createStatement().executeQuery(sql);
		
		StringBuffer sb = new StringBuffer();
		
		while(rs.next()){
			 String guid = rs.getString("ddzb_guid");
			 if(sb.length()>0){
				 sb.append(" union ");
			 }
			 sb.append("SELECT guid,sklx,skje FROM fwddsk WHERE (sklx='POS��' OR sklx='�ֽ�') AND zbguid='").append(guid).append("'");
		}
		
		double xj = 0.00;         //�ֽ�
		double pos = 0.00;        //pos��
		
		if(sb!=null && sb.length()>0){
			//System.out.println(sb.toString());
			rs = context.getConn().createStatement().executeQuery(sb.toString());
			while(rs.next()){
				if(rs.getString("sklx").equals("�ֽ�")){
					xj += rs.getDouble("skje");
				}
				else{
					pos +=rs.getDouble("skje");
				}
			}
		}
		
		context.put("xianjin", xj+"");
		context.put("posji", pos+"");
		
		return SUCCESS;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
