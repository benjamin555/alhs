package net.alhs.wf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.alhs.template.action.SelectJKZjeAction;
import net.alhs.template.yezhu.InsertYzsy;
import net.business.engine.common.TemplateContext;
import net.sysmain.util.GUID;
import net.sysmain.util.StringTools;
import net.sysplat.access.Authentication;
import net.sysplat.admin.eo.User;
import net.sysplat.common.Operator;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.DecisionHandler;

/**
 * 费用处理判断是否给借款人
 * 1.合并账户
 * 2.控制路由
 * @author hewei
 *
 */
public class FreeJudge implements DecisionHandler {

	private static final long serialVersionUID = 1L;

	public String decide(ExecutionContext context) throws Exception 
	{
		Token token = (Token)context.getToken();
		TemplateContext tContext = (TemplateContext)token.getTemplateContext();
		Operator operator = Authentication.getUserFromSession(tContext.getRequest());
		Connection conn = tContext.getConn();

		/**
		 * 1.业主账户的自动付款，并产生业主收益
		 * 2.费用账户的根据账号合并
		 */
		Map freeMap = new HashMap();

		InsertYzsy iy = new InsertYzsy();

		String [] jes = tContext.getReqParameterValues("je");               //金额
		String [] zhsx = tContext.getReqParameterValues("zhsx");            //账户属性
		String [] clmxGuids = tContext.getReqParameterValues("clmxGuids");  //处理明细Guid
		String [] zjmxGuids = tContext.getReqParameterValues("zjmxGuids");  
		String [] zhGuids = tContext.getReqParameterValues("zhGuids");      //账户Guid
		if(zhsx!=null && zhsx.length>0){
			for (int i = 0; i < zhsx.length; i++) {
				if(zhsx[i].equals("1")){       //付业主账户(直接支付)

					conn.createStatement().executeUpdate("update fwfyclmx set cnsj=now(),cn='"+operator.getName()+"' where guid='"+clmxGuids[i]+"'");

					iy.insertFreeToYzsy(zjmxGuids[i],tContext.getReqParameter("fycl_fylx"));
				}
				else if(zhsx[i].equals("3")){  //费用账户（合并账号）

					conn.createStatement().executeUpdate("update fwfyclmx set sfhb='1' where guid='"+clmxGuids[i]+"'");

					String zhGuid = zhGuids[i]; 
					if(freeMap.containsKey(zhGuid)){
						double je = Double.parseDouble((String)freeMap.get(zhGuid));
						je+=Double.parseDouble(jes[i]);
						freeMap.put(zhGuid, je+"");
					}
					else{
						freeMap.put(zhGuid,jes[i]);
					}
				}
			}

			/**
			 * 新增账号合并记录
			 */
			String guid = tContext.getReqParameter("guid");  //主表Guid
			String sql = "insert into fy_zhdjmx(guid,zbGuid,yhzhGuid,zje,sjfk) values(?,?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);

			Iterator it = freeMap.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry pairs = (Map.Entry)it.next();
				String yhzhGuid = (String)pairs.getKey();   //银行账号
				String money = (String)pairs.getValue();    //总金额

				ps.setString(1, new GUID().toString());
				ps.setString(2, guid);
				ps.setString(3, yhzhGuid);
				ps.setDouble(4, Double.parseDouble(money));
				ps.setDouble(5, Double.parseDouble(money));
				ps.addBatch();
			}
			ps.executeBatch();
		}



		/**
		 * 路由判断
		 */
		String [] lineName ={"结束","送借款人","送出纳"};
		String guid = tContext.getReqParameter("guid");  //费用主表Guid

		/**
		 * 1.全部是虚拟账户的会计直接完成，不需给出纳
		 */
		String sql = "select count(*) from fwfyclmx where zbGuid='"+guid+"' and cnsj is null";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		if(rs.next() && rs.getInt(1)==0){
			return lineName[0];
		}

		/**
		 * 2.合并费用账户的，有借款需给借款人填写借款金额，然后返回给会计
		 */

		List userList = new ArrayList();

		sql = "select b.xmid,b.xm from fy_zhdjmx a,yzyhxx b where a.yhzhGuid=b.guid and b.xmid is not null and a.zbGuid='"+guid+"'";
		rs = conn.createStatement().executeQuery(sql);
		while(rs.next()){
			String sqrguid = rs.getString(1);  //姓名id
			String name = rs.getString(2);     //姓名
			/**
			 * 查询借款金额，借款金额>0，则需要给借款人
			 */
			double je = StringTools.stringToDouble(SelectJKZjeAction.getJkje(conn, sqrguid));
			if(je>0){
				User user = new User();
				user.setName(name);
				user.setLoginid(sqrguid);
				userList.add(user);
			}
		}

		//将借款用户对象存入operator
		operator.setAttribute("userList", userList);


		return (userList!=null&&userList.size()>0)?lineName[1]:lineName[2];
	}
}
