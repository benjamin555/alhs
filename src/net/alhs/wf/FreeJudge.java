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
 * ���ô����ж��Ƿ�������
 * 1.�ϲ��˻�
 * 2.����·��
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
		 * 1.ҵ���˻����Զ����������ҵ������
		 * 2.�����˻��ĸ����˺źϲ�
		 */
		Map freeMap = new HashMap();

		InsertYzsy iy = new InsertYzsy();

		String [] jes = tContext.getReqParameterValues("je");               //���
		String [] zhsx = tContext.getReqParameterValues("zhsx");            //�˻�����
		String [] clmxGuids = tContext.getReqParameterValues("clmxGuids");  //������ϸGuid
		String [] zjmxGuids = tContext.getReqParameterValues("zjmxGuids");  
		String [] zhGuids = tContext.getReqParameterValues("zhGuids");      //�˻�Guid
		if(zhsx!=null && zhsx.length>0){
			for (int i = 0; i < zhsx.length; i++) {
				if(zhsx[i].equals("1")){       //��ҵ���˻�(ֱ��֧��)

					conn.createStatement().executeUpdate("update fwfyclmx set cnsj=now(),cn='"+operator.getName()+"' where guid='"+clmxGuids[i]+"'");

					iy.insertFreeToYzsy(zjmxGuids[i],tContext.getReqParameter("fycl_fylx"));
				}
				else if(zhsx[i].equals("3")){  //�����˻����ϲ��˺ţ�

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
			 * �����˺źϲ���¼
			 */
			String guid = tContext.getReqParameter("guid");  //����Guid
			String sql = "insert into fy_zhdjmx(guid,zbGuid,yhzhGuid,zje,sjfk) values(?,?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);

			Iterator it = freeMap.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry pairs = (Map.Entry)it.next();
				String yhzhGuid = (String)pairs.getKey();   //�����˺�
				String money = (String)pairs.getValue();    //�ܽ��

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
		 * ·���ж�
		 */
		String [] lineName ={"����","�ͽ����","�ͳ���"};
		String guid = tContext.getReqParameter("guid");  //��������Guid

		/**
		 * 1.ȫ���������˻��Ļ��ֱ����ɣ����������
		 */
		String sql = "select count(*) from fwfyclmx where zbGuid='"+guid+"' and cnsj is null";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		if(rs.next() && rs.getInt(1)==0){
			return lineName[0];
		}

		/**
		 * 2.�ϲ������˻��ģ��н������������д����Ȼ�󷵻ظ����
		 */

		List userList = new ArrayList();

		sql = "select b.xmid,b.xm from fy_zhdjmx a,yzyhxx b where a.yhzhGuid=b.guid and b.xmid is not null and a.zbGuid='"+guid+"'";
		rs = conn.createStatement().executeQuery(sql);
		while(rs.next()){
			String sqrguid = rs.getString(1);  //����id
			String name = rs.getString(2);     //����
			/**
			 * ��ѯ���������>0������Ҫ�������
			 */
			double je = StringTools.stringToDouble(SelectJKZjeAction.getJkje(conn, sqrguid));
			if(je>0){
				User user = new User();
				user.setName(name);
				user.setLoginid(sqrguid);
				userList.add(user);
			}
		}

		//������û��������operator
		operator.setAttribute("userList", userList);


		return (userList!=null&&userList.size()>0)?lineName[1]:lineName[2];
	}
}
