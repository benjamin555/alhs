package net.alhs.template.action.fwgl;
/**
 * 添加无主房 栋、楼层、房号不同，则复制
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.alhs.api.HouseAction;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.business.template.action.SerialNumberAction;
import net.sysmain.common.EngineTools;
import net.sysmain.util.GUID;
import net.sysplat.access.Authentication;
import net.sysplat.common.Operator;

public class CopyHouseModel implements I_TemplateAction {

	public int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest()); // 封装回话对象
		String guid = context.getReqParameter("guid");
		String type = context.getReqParameter("fwzb_fwzt");
		String gzlzt = context.getReqParameter("fwzb_gzlzt");
		
		String dong = context.getReqParameter("fwzb_dong");
		String louceng = context.getReqParameter("fwzb_louceng");
		String fjh = context.getReqParameter("fwzb_fjh");
		String xxdz = context.getReqParameter("fwzb_xxdz");
		
		
		/**
		 * 产生单号
		 */
		new SerialNumberAction().execute(context);

		//类型非无主房 、暂存、新增  
		if(!type.equals("3") || gzlzt.equals("0") || context.getTemplatePara().getEditType()!=1)
		{
			return SUCCESS;
		}
		
		Connection conn = context.getConn();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String _dong = null;
		String _louceng = null;
		String _fjh = null;
		
		String sql = "select dong,louceng,fjh from house where guid =?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, guid);
		rs = ps.executeQuery();
		if(rs.next()){
			_dong = rs.getString("dong");
			_louceng = rs.getString("louceng");
			_fjh = rs.getString("fjh");
		}
		
		//栋 楼层 房间号 改变 则复制
		if(!dong.trim().equals(_dong) || !louceng.trim().equals(_louceng) || !fjh.trim().equals(_fjh)){
			EngineTools tools = new EngineTools(context);
			
			String newGuid = new GUID().toString();
			
			//房屋主表
			String houseSql = "insert into house(guid,sqbmguid,sqrguid,gzlzt,sqsj,sqr,fwzt,zbh,htms,flid,fjmc,qtGuid,sf,cs,qu,jd,xqhy,fangxing,dong,louceng,fjh,xxdz,fx,fwmj,rsxz,fxGuid)" +
					          "select '"+newGuid+"' as guid,sqbmguid,sqrguid,gzlzt,sqsj,sqr,fwzt,'Z"+tools.dateSerial(39,3)+"' as zbh,htms,flid,fjmc,qtGuid,sf,cs,qu,jd,xqhy,fangxing,'"+dong+"' as dong,'"+louceng+"' as louceng,'"+fjh+"' as fjh,'"+xxdz+"' as xxdz,fx,fwmj,rsxz,fxGuid from house where guid='"+guid+"'";
			
			//房屋其他信息
			String qtxxSql = "insert into fwqtxx(guid,houseGuid,qtmc,qtdh1,qtdh2,qtdz,qtfzrxm,qtfzrsj,qtfzrdh,qtzrrxm,qtzrrsj,qtzrrdh,zxmc,zxdh1,zxdh2,zxdz,zxfzrxm,zxfzrsj,zxfzrdh,zxzrrxm,zxzrrsj,zxzrrdh)" +
					         "select '"+new GUID().toString()+"' as guid,'"+newGuid+"' as houseGuid,qtmc,qtdh1,qtdh2,qtdz,qtfzrxm,qtfzrsj,qtfzrdh,qtzrrxm,qtzrrsj,qtzrrdh,zxmc,zxdh1,zxdh2,zxdz,zxfzrxm,zxfzrsj,zxfzrdh,zxzrrxm,zxzrrsj,zxzrrdh from fwqtxx where houseGuid='"+guid+"'";
			
			sql = "select guid,houseGuid,mj,chuang,rs,jc,wei,jg from fwwlxx where houseGuid = '"+guid+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				//房屋屋内信息
				ps.addBatch("insert into fwwlxx(guid,houseGuid,mj,chuang,rs,jc,wei,jg) select '"+new GUID().toString()+"' as guid,'"+newGuid+"' as houseGuid,mj,chuang,rs,jc,wei,jg from fwwlxx where guid='"+rs.getString("guid")+"'");
			}
			
			ps.addBatch(houseSql);
			ps.addBatch(qtxxSql);
			ps.executeBatch();
			
			/**
			 * 无主房另存后同步数据
			 */
			HouseAction.addHouseToWeb(newGuid, false);
			
			return END;
		}
		
		//ps.addBatch("update house set gzlzt=2 where guid='"+guid+"'");
		//ps.addBatch();
		
		
		return SUCCESS;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
