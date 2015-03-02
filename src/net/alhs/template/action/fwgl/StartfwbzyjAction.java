package net.alhs.template.action.fwgl;
/**
 * 房屋布置前显示数据
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.sysmain.util.GUID;
import net.sysplat.access.Authentication;
import net.sysplat.common.Operator;

public class StartfwbzyjAction implements I_TemplateAction {

	public int execute(TemplateContext context) throws Exception {
		Operator op = Authentication.getUserFromSession(context.getRequest()); // 封装回话对象
		String guid = context.getReqParameter("guid");
		int type = Integer.parseInt(context.getReqParameter("type"));
		String str = null;
		String sqlStr = null; 

		switch (type) {
		case 1: str="装修"; sqlStr=" zx,zxsj,zxfzryj "; break;
		case 2: str="电器"; sqlStr=" dq,dqsj,dqfzryj "; break;
		case 3: str="家私"; sqlStr=" js,jssj,jsfzryj "; break;
		case 4: str="装饰"; sqlStr=" zs,zssj,zsfzryj "; break;
		case 5: str="巾单"; sqlStr=" wd,wdsj,wdfzryj "; break;
		case 6: str="消耗品"; sqlStr=" xhp,xhpsj,xhpfzryj "; break;
		case 7: str="房间照"; sqlStr=" fjz,fjzsj,fjzfzryj "; break;
		case 8: str="环境照"; sqlStr=" hjz,hjzsj,hjzfzryj "; break;
		case 9: str="文案"; sqlStr=" wa,wasj,wafzryj "; break;
		}  
		String sql = "select guid,"+sqlStr+" from fwrz where houseGuid='"+guid+"'";
		Connection conn = context.getConn();
		ResultSet rs = conn.createStatement().executeQuery(sql);
		String t1=null,t2=null,t3=null,t4=null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date()).toString();
		if(rs.next()) {
			t1=rs.getString(1);
			t2=rs.getString(2);
			if(t2==null||"".equals(t2)){t2=op.getName();	}	//日期为空
			t3 = rs.getString(3);
			if(t3==null||"".equals(t3)){t3=date;	}else {t3 = sdf.format(rs.getTimestamp(3));}	//日期为空
			
			t4=rs.getString(4);
		}else {
			t1 = new GUID().toString();
			t2 = op.getName();   		 
			t3 = date;
			t4 ="";
			conn.createStatement().executeUpdate("insert into fwrz(guid,houseGuid) values('"+t1+"','"+guid+"')");
		}
		context.put("str", str);
		context.put("t1", t1);
		context.put("t2", t2);
		context.put("t3", t3);
		context.put("t4", t4);
		context.put("type", type+"");
		return SUCCESS;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
