package net.alhs.common;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import net.alhs.api.HouseBean;
import net.chysoft.common.ShutCutFactory;
import net.sysmain.common.ConnectionManager;

/**
 * ���Ͷ�����ض���
 * @author Administrator
 *
 */
public class MsgInfo{

	/**
	 * ���Ͷ������ţ��Ѷ� Ԥ�� ������
	 * @param ddGuid
	 * @throws Exception
	 */
	public static void sendOrderMsg(String ddGuid) throws Exception {
		Connection conn = null; 
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select houseGuid,status,sqdh,xm,lxdh,rzrq,tfrq,rzts,zdqxsj_xs,zdqxsj_fz,ffxj from fwddzb where guid=?";
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, ddGuid);
			rs = ps.executeQuery();
			if(rs.next()){
				String houseGuid = rs.getString("houseGuid");
				String ddzt = rs.getString("status");                                          //����״̬��4���Ѷ� 5��Ԥ�� 6��������
				String sqdh = rs.getString("sqdh");                                            //����
				String xm = rs.getString("xm");                                                //ҵ������
				String lxdh = rs.getString("lxdh");                                            //��ϵ�绰
				String begindate = rs.getString("rzrq");                                       //��ס����
				String enddate = rs.getString("tfrq");                                         //�˷�����
				String rzts = rs.getString("rzts");                                            //����
				String zdqxsj_xs = rs.getString("zdqxsj_xs");
				String zdqxsj_fz = rs.getString("zdqxsj_fz"); 
				double zje = rs.getDouble("ffxj");                                             //�ܽ��

				HouseBean house = getHouseInfo(houseGuid);                                     //������Ϣ

				/**
				 * ���Ͷ���(�Ѷ� Ԥ�� ����)
				 */
				int id = 8;         //����ģ��ID 
				String [] values  = null;
				if(ddzt.equals("4")){
					id = 7;
					values = new String []{xm,sqdh,house.getHomeaddress(),house.getFangxing(),begindate,enddate,rzts,zje+"",house.getQtmc(),house.getJtjs()};
				}
				else if(ddzt.equals("5") || ddzt.equals("6")){
					String ydOrSd = ddzt.equals("5")?"Ԥ��":"����";
					String qxsj = begindate+" "+zdqxsj_xs+":"+zdqxsj_fz;
					values = new String []{xm,ydOrSd,sqdh,house.getFjjs(),house.getFangxing(),begindate,enddate,rzts,zje+"",ydOrSd,qxsj,house.getQtmc(),house.getJtjs()};
				}

				if(values != null && values.length>0){
					if(lxdh!=null && !lxdh.trim().equals("")){    //�绰����Ϊ�ղ�����
						String content = MsgTemplate.getContent(id, values);
						new ShutCutFactory().sendMobileMessage(new String[]{lxdh.trim()}, content, -1, null, null);
					}
				}
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			ConnectionManager.close(conn);
		}
	}
	/**
	 * ���Ͷ�����ַ����
	 * @param ddGuid
	 * @throws Exception
	 */
	public static void sendOrderAddressMsg(String ddGuid) throws Exception {
		Connection conn = null; 
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select houseGuid,xm,lxdh from fwddzb where guid=?";
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, ddGuid);
			rs = ps.executeQuery();
			if(rs.next()){
				String houseGuid = rs.getString("houseGuid");
				String xm = rs.getString("xm");                                                //ҵ������
				String lxdh = rs.getString("lxdh");                                            //��ϵ�绰

				HouseBean house = getHouseInfo(houseGuid);                                     //������Ϣ
				/**
				 *  ���Ͷ���(��ַ)
				 */
				int msgId = 13;      //����ģ��ID 
				String [] vs = new String[]{xm,house.getQtmc(),house.getJtjs()};
				if(house.getQtmc().indexOf("�̹�԰")!=-1){
					msgId = 14;
				}
				else if(house.getQtmc().indexOf("˫����")!=-1){
					msgId = 15;
				}

				if(lxdh!=null && !lxdh.trim().equals("")){    //�绰����Ϊ�ղ�����
					String content = MsgTemplate.getContent(msgId, vs);
					new ShutCutFactory().sendMobileMessage(new String[]{lxdh.trim()}, content, -1, null, null);
				}

			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			ConnectionManager.close(conn);
		}
	}
	/**
	 * ��ȡ����������еķ�����Ϣ
	 * @param houseGuid
	 * @return
	 * @throws Exception
	 */
	public static HouseBean getHouseInfo(String houseGuid) throws Exception {
		Connection conn = null; 
		PreparedStatement ps = null;
		String sql = "SELECT CONCAT(h.sf,h.cs,h.qu,IFNULL(h.jd,''),IFNULL(h.xqhy,'')) simpleAddress,xxdz,fangxing,q.mc,q.dh1 FROM house h,qtjzx q WHERE h.qtGuid = q.guid AND h.guid=?";
		HouseBean house = new HouseBean();
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, houseGuid);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				house.setHomeaddress(rs.getString("xxdz"));    //������ϸ��ַ
				house.setFjjs(rs.getString("simpleAddress"));  //���ݼ򵥵�ַ
				house.setFangxing(rs.getString("fangxing"));   //����
				house.setQtmc(rs.getString("mc"));             //ǰ̨��
				house.setJtjs(rs.getString("dh1"));            //ǰ̨�绰
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			ConnectionManager.close(conn);
		}

		return house;
	}
}
