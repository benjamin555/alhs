package net.kml.template.action;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.sysmain.common.I_CommonConstant;
import net.sysmain.util.GUID;
import net.sysplat.access.Authentication;
import net.sysplat.common.Operator;

public class MaterielNumberLog implements I_TemplateAction{
   
	public int execute(TemplateContext context) throws Exception 
    {
		HttpServletRequest request = context.getRequest();
		Connection conn = context.getConn();
		String wlGuid = request.getParameter("guid");
		String lhmc = request.getParameter("lhmc");
        //β��
        String wh = request.getParameter("wh");
		
        /**
         * �ж��Ϻ��ظ�����
         */
        ResultSet rs1 = conn.createStatement().executeQuery("select count(*) from goods where guid<>'" + wlGuid + "' and lh='" + lhmc  + "'" +
                " and wh='" + wh + "'");
        if(rs1.next() && rs1.getInt(1) > 0)
        {
            context.getRequest().setAttribute(I_CommonConstant.JAVA_SCRIPT_CODE, "alert(\"�Ѿ������ظ����Ϻ�[" + lhmc + "]��β��Ϊ" + wh + "\")");
            return FAILURE;
        }

		PreparedStatement ps = conn.prepareStatement("select lh from goods where guid=?");
		ps.setString(1, wlGuid);
		ResultSet rs = ps.executeQuery();

		if(rs.next()){
			String lh = rs.getString(1);      //�Ϻ�
			if(lh==null || lh.equals("")){
				doInsert(context,wlGuid,lhmc,"���","");
			}
			else if(!lh.equals(lhmc)){
				doInsert(context, wlGuid,lh,"��������","�Ϻţ�["+lh+"] ����Ϊ ["+lhmc+"]");
			}

		}
		return SUCCESS;
	}
	private void doInsert(TemplateContext context,String wlGuid,String cwmc,String type,String remark) throws Exception{
		Operator operator = Authentication.getUserFromSession(context.getRequest());
		String sql = "insert into cwrz(guid,wlGuid,cwh,cgy,qdsj,cz,bz,type) values(?,?,?,?,now(),?,?,'2')";
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

	public String getErrorMessage() 
    {
		return null;
	}

}
