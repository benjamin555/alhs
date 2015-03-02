package net.alhs.template.action.order;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.alhs.template.yezhu.InsertYzsy;
import net.sysmain.common.ConnectionManager;
/**
 * ������ط��ü���
 * @author Administrator
 *
 */
public class OrderFreeAction{

	/**
	 * ���㶩����ط�����Ϣ��Ӷ����ɣ��ֳɣ�����ȣ�
	 * @param context
	 * @throws Exception
	 */
	public static void initOrderFree(String ddGuid) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try{
			conn = ConnectionManager.getInstance().getConnection();
			
			/*
			 * ��ɾ���������¼��㣨�޸�ƽ̨�����Ϣ����ƽ̨������δ�����Ӷ�����¼��㣩
			 */
			ps = conn.prepareStatement("delete from ddhs where guid='"+ddGuid+"'");
			ps.executeUpdate();
			
			/**
			 * ������ط���
			 */
			ps = conn.prepareStatement("select a.ywyid,a.ddsx,a.ffyj,a.jff,a.ffxj,b.fcbl,b.qtGuid,b.htms from fwddzb a,house b where a.houseGuid=b.guid and a.guid=? ");
			ps.setString(1, ddGuid);
			rs = ps.executeQuery();
			if(rs.next()){
				String ywyid = rs.getString("ywyid");             //ҵ��Աid
				String ddsx = rs.getString("ddsx");               //��������
				double ffyj = rs.getDouble("ffyj");               //����ԭ�ۣ��������۸�
				double jff = rs.getDouble("jff");                 //������  ��ȷ�Ϸ��ۣ�
				double ffxj = rs.getDouble("ffxj");               //����С�ƣ���ȥ�Żݺ��ۿۣ�
				double fcbl = rs.getDouble("b.fcbl");             //���ݵķֳɱ���
				String qtGuid = rs.getString("b.qtGuid");         //ǰ̨Guid
				String htms = rs.getString("b.htms");             //��ͬģʽ

				/**
				 * ��ѯҵ�����ϵ��
				 */
				//double ywtcxs = 0;           //ҵ�����ϵ��
				//if(ywyid!=null && !ywyid.trim().equals("")){
					//rs = conn.createStatement().executeQuery("select ywtcxs from employee_archive where LoginId='"+ywyid+"'");
					//if(rs.next()){
						//ywtcxs = rs.getDouble("ywtcxs");
					//}
				//}
				
				/**
				 * ��ѯӶ����ʣ���ѡӶ�����
				 */
				String ptxxGuid = null;       //ƽ̨��ϢGuid
				double yjbl = 0;              //Ӷ�����
				double hxyl = 0;              //��ѡӶ��
				String sffy = "";             //�Ƿ�Ҫ��Ӷ
				String sql = "select guid,yjl,hxyl,sffy from ptxx where qtGuid=? and ptmc=?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, qtGuid);
				ps.setString(2, ddsx);
				rs = ps.executeQuery();
				if(rs.next()){
					ptxxGuid = rs.getString("guid");
					yjbl = rs.getDouble("yjl");
					hxyl = rs.getDouble("hxyl");
					sffy = rs.getString("sffy");
				}

				double ptyj = jff*(yjbl/100);                    //ƽ̨Ӷ��
				double hxyj = jff*(hxyl/100);                    //��ѡӶ��
				double syxj = ffxj-ptyj-hxyj;                    //����С�ƣ�����С��-Ӷ��-��ѡӶ��
				
				double syb = syxj/(ffyj==0?1:ffyj)*100;          //����ȣ�����С��/����ԭ��*100��
				double fcje = syxj*(fcbl/100);                   //�ֳɽ��
				//double ywtcje = syxj*(ywtcxs/100);               //ҵ����ɽ��
				//double ddsy = syxj-fcje-ywtcje;                  //��������

				sql = "insert into ddhs(guid,ptxxGuid,yjl,ptyj,hxyl,hxyj,fcbl,fcje,ywtcxs,ywtcje,ddsy,syxj,syb,qrptyj,qrhxyj,sffy_pt,sffy_hx) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, ddGuid);
				ps.setString(2, ptxxGuid);
				ps.setDouble(3, yjbl);
				ps.setDouble(4, ptyj);
				ps.setDouble(5, hxyl);
				ps.setDouble(6, hxyj);
				ps.setDouble(7, fcbl);
				ps.setDouble(8, fcje);
				ps.setDouble(9, 0);
				ps.setDouble(10,0);
				ps.setDouble(11,0);
				ps.setDouble(12, syxj);
				ps.setDouble(13, syb);
				ps.setDouble(14, ptyj);
				ps.setDouble(15, hxyj);
				ps.setString(16, sffy.equals("1")?"0":"2");    //ƽ̨��Ϣ�Ƿ�Ӷ=0�����������򶩵���Ӷ״̬=2����ɸ�Ӷ��
				ps.setString(17, sffy.equals("1")?"0":"2");

				ps.executeUpdate();
				
				/**
				 * �������
				 */
				calcOrderBonus(ddGuid);
				
				/**
				 * �ֳɽ����ҵ���˻����ֳɷ���
				 */
				if(htms.equals("2")){
					InsertYzsy iy = new InsertYzsy();
					iy.addOrderFencheng(ddGuid);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
	}
	/**
	 * ���㽱��
	 * @param ddGuid
	 * @throws Exception
	 */
	public static void calcOrderBonus(String ddGuid) throws Exception{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try{
			conn = ConnectionManager.getInstance().getConnection();
			String sql = "select d.ddsx,h.qtGuid,s.syb,s.syxj from fwddzb d,house h,ddhs s where d.houseGuid=h.guid and d.guid=s.guid and d.guid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, ddGuid);
			rs = ps.executeQuery();
			if(rs.next()){
				String ddsx = rs.getString("ddsx");      //ƽ̨����
				String qtGuid = rs.getString("qtGuid");  //�Ƶ�
				
				double syb = rs.getDouble("syb");        //�����������
				double syxj = rs.getDouble("syxj");      //����С��
				
				double tcbl = 0.00;                      //��ɱ���
				
				sql = "select * from tcmbmx where zbGuid = (select tcmbGuid from ptxx where qtGuid=? and ptmc=?)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, qtGuid);
				ps.setString(2, ddsx);
				rs = ps.executeQuery();
				while(rs.next()){
					double syb1 = rs.getDouble("syb1");
					double syb2 = rs.getDouble("syb2");
					double bl = rs.getDouble("tcbl");
					
					if(syb1<syb && syb<=syb2){
				        tcbl = bl;
				        break;
					}
				}
				
				double bonus = syxj*(tcbl/100);         //���� 
				
				/**
				 * ���㶩������ɱ��ʣ����𣬶�������
				 */
				sql ="update ddhs set ywtcxs=?,ywtcje=?,ddsy=syxj-fcje-? where guid=?";
				ps = conn.prepareStatement(sql);
				ps.setDouble(1, tcbl);
				ps.setDouble(2, bonus);
				ps.setDouble(3, bonus);
				ps.setString(4, ddGuid);
				ps.executeUpdate();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
	}


	public String getErrorMessage() {
		return null;
	}
}
