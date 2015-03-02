package net.alhs.common;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sysmain.common.ConnectionManager;
import net.sysplat.common.I_ResourceConstant;
import net.sysplat.common.Operator;
import net.sysplat.common.Util;
import net.sysmain.util.GUID;
import net.sysmain.util.StringTools;
import net.sysplat.access.Authentication;

public class CalendarAction implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    
    public void doAction(HttpServletRequest request, HttpServletResponse response)
    {
        Operator operator = Authentication.getUserFromSession(request);
        String actionType = request.getParameter("actionType");
        String message = null;
        Connection conn = null;
        
        if(operator == null)
        {
            request.setAttribute(I_ResourceConstant.ALERT_SCRIPT, "alert(\"系统超时，请重新登录访问\");");
        }
        else
        {
            try
            {
                conn = ConnectionManager.getInstance().getConnection();
                ConnectionManager.setAutoCommit(conn, false);
                if("save".equals(actionType))
                {
                    this.saveSetting(conn, request, operator);
                }
                else if("del".equals(actionType))
                {
                    this.deleteSetting(conn, request, operator);
                }
                ConnectionManager.commit(conn);
            }
            catch(Exception ex)
            {
                ConnectionManager.rollback(conn);
                ex.printStackTrace();
                request.setAttribute(I_ResourceConstant.ALERT_SCRIPT, "alert(\"添加日期类型失败,\\\n" +
                        Util.toJavaScript(ex.getMessage())+ "\");");
            }
            finally
            {
                ConnectionManager.close(conn);
            }
        }
        if(message != null && !message.equals("")) 
        {
            request.setAttribute(I_ResourceConstant.ALERT_MESSAGE, message);
        }
        
        try
        {
            request.getRequestDispatcher("/sysplat/message.jsp").forward(request, response);
        }
        catch(Exception ex)
        {
            ;
        }
    }
    
    /**
     * 增加设置
     * @param conn
     * @param request
     * @param operator
     * @throws Exception
     */
    private void saveSetting(Connection conn, HttpServletRequest request, Operator operator) throws Exception
    {
    	String rlGuid = request.getParameter("rlGuid");      //日历Guid
        String date = request.getParameter("dealDate");    //日期
        String title = request.getParameter("title");      //类型
        
        String sql = "Select * from rqsz where nyr='" + date + "' and rlGuid='"+ rlGuid +"'";
        ResultSet rs =  conn.createStatement().executeQuery(sql);
        PreparedStatement ps = null;
        if(rs.next())
        {
            ps = conn.prepareStatement("Update rqsz set rqlx=? where guid=?");
            ps.setString(1, title);
            ps.setString(2, rs.getString("guid"));
            ps.execute();
        }
        else
        {
            ps = conn.prepareStatement("Insert into rqsz(guid,rlGuid,nyr,rqlx) values(?,?,?,?)");
            ps.setString(1, new GUID().toString());
            ps.setString(2, rlGuid);
            ps.setDate(3, StringTools.stringToDate(date));
            ps.setString(4, title);
            ps.execute();
        }
        
        request.setAttribute(I_ResourceConstant.ALERT_SCRIPT, "parent.doRefresh()");
    }
    
    /**
     * 删除设置
     * @param conn
     * @param request
     * @param operator
     * @throws Exception
     */
    private void deleteSetting(Connection conn, HttpServletRequest request, Operator operator) throws Exception
    {
    	String rlGuid = request.getParameter("rlGuid");
    	
        String date = request.getParameter("dealDate");
        
        conn.createStatement().execute("Delete from rqsz where nyr='" + date + "' and rlGuid='"+rlGuid+"'");
        
        request.setAttribute(I_ResourceConstant.ALERT_SCRIPT, "parent.doRefresh()");
    }
    
    /**
     * 获取已有的设置
     * @param year
     * @param month
     * @param result
     * @return
     */
    public String getHolidaySetting(int year1, int month1, HashMap result,String rlGuid)
    {
        //简单处理，前后延长6天
        StringBuffer sb = new StringBuffer("var holidays = new Array();\r\n");
        Connection conn = null;

        int year= year1;
        int month= month1-1;
        
        if(month == 0) 
        {
            month = 12;
            year = year - 1;
        }
        String startDate = year + "-" +  ((month<10)?"0"+month:month+"") + "-01";
        year= year1;
        month = month1 +1;
        if(month == 13) 
        {
            month = 1;
            year = year + 1;
        }
        String endDate = year + "-" +  ((month<10)?"0"+month:month+"") + "-31";
        String sql = "Select * from rqsz where nyr>='" + startDate + "' and nyr<='" + endDate + "' and rlGuid='"+rlGuid+"'";
       
        try
        {
            conn = ConnectionManager.getInstance().getConnection();
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while(rs.next())
            {
                String dt = StringTools.dateToString(rs.getDate("nyr"), false);
                sb.append("holidays[\"").append(dt).append("\"] = ");
                sb.append("[");
                //类型
                sb.append("\"").append(StringTools.toJavaScript(rs.getString("rqlx"))).append("\",");
                sb.append("]\r\n");
                result.put(dt, new String[]{rs.getString("rqlx")});
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            ConnectionManager.close(conn);
        }
        this.result = result;
        return sb.toString();
    }
    
    private  HashMap result =null;
    private boolean isHaveContent = false;
    private StringBuffer html = new StringBuffer();
    
    public String getDataHtml(int y, int m, int d)
    {
    	
        if(this.result.size() == 0) {isHaveContent=false;return "&nbsp;";}
        
        String key = y + "-" +  ((m<10)?"0"+m:m+"") + "-" + ((d<10)?"0"+d:d+"");
        //System.out.println("gkey:" + key);
        String[] temp = (String[])result.get(key);
        if(temp == null) {isHaveContent=false;return "&nbsp;";}
        
        if(html.length() > 0) html.delete(0, html.length());
        
        if(temp[0] != null && !"".equals(temp[0])) html.append(temp[0]);
        
        isHaveContent=true;
        
        return html.toString();
    }
    
    public String getDatePrice(int y, int m, int d, HashMap map)
    {
    	
        if(map == null ||map.size() == 0) {isHaveContent=false;return "&nbsp;";}
        
        String key = y + "-" +  ((m<10)?"0"+m:m+"") + "-" + ((d<10)?"0"+d:d+"");
        //System.out.println("gkey:" + key);
        FangTaiObject temp = (FangTaiObject)map.get(key);
        if(temp == null) {isHaveContent=false;return "&nbsp;";}
        
        if(html.length() > 0) html.delete(0, html.length());
        
        if(temp.getPrice() != null && !"".equals(temp.getPrice())) html.append("￥:"+temp.getPrice());
        
        isHaveContent=true;
        
        return html.toString();
    }
}
