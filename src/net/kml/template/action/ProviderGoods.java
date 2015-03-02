package net.kml.template.action;

import java.sql.ResultSet;

import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.sysmain.common.EngineTools;
import net.sysmain.common.exception.TemplateMessageException;

/**
 * ���ع�Ӧ�̿��ṩ������
 * @author Owner
 *
 */
public class ProviderGoods implements I_TemplateAction
{
    private String errMessage = null;
    
    public int execute(TemplateContext context) throws Exception
    {
        /**
         * �ж�Ȩ��
         */
        EngineTools tools = new EngineTools(context);
        //if(tools.isPermit("S[258/2]") == 0 && tools.isPermit("S[260/8192]") == 0)
        //{
            //throw new TemplateMessageException("��ǰ�û�û�з���Ȩ��");
        //}
        
        String sql = context.getListObjectPara().getQuerySql();
        
        sql = sql.replaceFirst("select ", "select distinct ");
        context.getListObjectPara().setTransitionSql(sql);
        
        
        /**
         * ����������Ӧ�̵���Ϣ
         */
        String wl = context.getReqParameter("wl");
        if(wl != null)
        {
            //��ǰ��Ӧ�̵�guid
            String guid = context.getReqParameter("guid");
            sql = "select distinct suppliers,suppliersGUID from stockStorage where materialsGuid='" + wl + "'";
            ResultSet rs = context.getConn().createStatement().executeQuery(sql);
            StringBuffer buf = new StringBuffer("");
            while(rs.next())
            {
                if(rs.getString("suppliersGUID").equals(guid)) continue;
                buf.append("&nbsp;<a style=\"color:#0000D0;text-decoration:none\" href=\"gettemplate.jsp?temp_Id=220&guid=").append(rs.getString("suppliersGUID")).append("&wl=").append(wl).append("\">")
                    .append(rs.getString("suppliers")).append("</a>");
            }
            
            if(buf.length() > 0) 
            {
                buf.insert(0, "<div style=\"height:22\">&nbsp;<b>������Ӧ�̣�</b>").append("</div>");
                context.put("qtgys", buf.toString());
                context.put("wl", "&wl=" + wl);
            }
        }
        
        return SUCCESS;
    }

    public String getErrorMessage()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
