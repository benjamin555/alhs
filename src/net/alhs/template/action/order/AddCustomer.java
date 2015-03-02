package net.alhs.template.action.order;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import net.mail.util.GUID;
import net.sysmain.common.ConnectionManager;
import net.sysmain.util.StringTools;
/**
 * ������ס�Զ������˿�
 * @author Administrator
 *
 */
public class AddCustomer{

	public static void addCustomer(String rzrGuid){
		//Operator op = Authentication.getUserFromSession(context.getRequest()); // ��װ�ػ�����
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;


		try 
		{
			String xm = "",sj = "",sfz = "",dz = "",xp = "",bz = "",sqsj = "";
  
			double ffxj = 0;
			
			conn = ConnectionManager.getInstance().getConnection();
			String sql = "select a.*,b.ffxj,b.sqsj from ddrzr a,fwddzb b  where a.zbGuid=b.guid and a.guid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1,rzrGuid);
			rs = ps.executeQuery();
			if(rs.next()){
				xm = rs.getString("xm");
			    sfz = rs.getString("sfz");
			    sj = rs.getString("lxdh");
			    dz = rs.getString("dz");
			    xp = rs.getString("sfzxp");
			    bz = rs.getString("bz");
			    
			    sqsj = rs.getString("b.sqsj");
			    ffxj = rs.getDouble("b.ffxj");
			}
			
			if(sfz!=null && !sfz.trim().equals("")){
				sql = "select guid,zjddsj,dds from gkzb where sfz = ?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, sfz);
				rs = ps.executeQuery();
				/**
				 * �Ѵ��ڸ����֤��ͬ�Ŀͻ�
				 *  1,������+1����������ۼ�
				 *  2���޸������������
				 *  3���޸������������
				 */
				if(rs.next()){
					/**
					 * ͬһ�충������ֻ��һ��
					 */  
					String gkGuid = rs.getString("guid");          
					String zjddsj = rs.getString("zjddsj");            //�������ʱ��
					int dds = rs.getInt("dds");                        //��������
					if(!zjddsj.equals(sqsj.substring(0, 10))){
						dds++;         //������+1
						
						//��Ա����
						String jb = "1";       //ͭ��
						if(dds>=5 && dds<=20){
							jb = "2";          //����
						}
						else if(dds>20 && dds<=50){
							jb = "3";          //����
						}
						else if(dds>50){
							jb = "4";          //��ʯ
						}
					
						sql = "update gkzb set dds=?,ddje=ifnull(ddje,0)+?,zjddsj=?,zjddts=DATEDIFF(NOW(),'"+sqsj+"'),jibie=? where guid=?";
						ps = conn.prepareStatement(sql);
						ps.setInt(1, dds);
						ps.setDouble(2, ffxj);
						ps.setString(3, sqsj.substring(0, 10));
						ps.setString(4, jb);
						ps.setString(5, gkGuid);
						ps.executeUpdate();
					}
				}
				
				else{
					sql = "insert into gkzb(guid,xm,sj,sfz,sheng,shi,sfzxp,dz,bz,lrsj,dds,ddje,zjddsj,zjddts,jibie) values(?,?,?,?,?,?,?,?,?,now(),1,?,?,DATEDIFF(NOW(),'"+sqsj+"'),'1')";
					ps = conn.prepareStatement(sql);
					ps.setString(1, new GUID().toString());
					ps.setString(2, xm);
					ps.setString(3, sj);
					ps.setString(4, sfz);
					ps.setString(5, (dz==null || dz.trim().equals("") || dz.indexOf("ʡ")==-1)?"":dz.substring(0,dz.indexOf("ʡ")));
					ps.setString(6, (dz==null || dz.trim().equals("") || dz.indexOf("��")==-1)?"":dz.substring((dz.indexOf("ʡ")==-1?0:dz.indexOf("ʡ")+1),dz.indexOf("��")));
					ps.setString(7, xp);
					ps.setString(8, dz);
					ps.setString(9, bz);
					ps.setDouble(10, ffxj);
					ps.setString(11, StringTools.getCurrentDateTime().substring(0, 10));
					
					ps.executeUpdate();
					
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();

		}
		finally 
		{
			ConnectionManager.close(conn);
		}
	}
}