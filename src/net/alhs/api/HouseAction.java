package net.alhs.api;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;

import net.business.db.CodeFactory;
import net.sysmain.common.ConnectionManager;
/**
 * ����ӿ���
 * @author Administrator
 *
 */
public class HouseAction{

	private static CodeFactory cf = new CodeFactory();

	/**
	 * ����SQL
	 */
	private static final String sql = "INSERT INTO ht_mall(areaid,title,price,thumb,thumb1,thumb2,homedevice,guid,catid,hometicket,addtime,adddate," +
	"garden,hotelkefu,hoteltype,building,ground,roomno," +
	"relate_name,relate_id,relate_title,n1,n2,n3,v1,v2,v3,express_name_1,fee_start_1,fee_step_1,express_name_2,fee_start_2,fee_step_2,express_name_3,fee_start_3,fee_step_3,homearea," +
	"huanjing,yule,traffic,shopping,trippoint,username,groupid,company,validated,status,edittime,editdate," +
	"roomtype,roomarea,bedno,imageid,xuzhi,discount,homeaddress,number,couldorder,setmodel,level,datedata,fangshu,bargaintype,divide,bedtype,jingguan,city,modeid) " +
	"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
	"'','','','','','','','','','',0.00,0.00,'',0.00,0.00,'',0.00,0.00,0,?,?,?,?,?,?,?,?,1,3,?,now(),?,?,?,0,'',?,?,?,'',?,?,'',?,?,?,?,?,?,?)";

	/**
	 * �޸�SQL
	 */
	private static final String uSql = "update ht_mall set areaid=?,title=?,price=?,catid=?,hometicket=?,edittime=?,editdate=now(),roomtype=?,roomarea=?,bedno=?,username=?,company=?,groupid=?,discount=?,homeaddress=?,number=? " +
	",garden=?,hotelkefu=?,hoteltype=?,building=?,ground=?,roomno=?,bargaintype=?,divide=?,bedtype=?,jingguan=?,city=?,modeid=? where guid=?";

	/**
	 * ��ѯ����SQL
	 */
	private static final String querySql = "SELECT h.*,q.mc,f.chuang,f.jc,f.jg FROM house h,qtjzx q,fwwlxx f WHERE h.qtGuid=q.guid  AND h.guid = f.houseGuid and h.guid=?";

	private static String ADD = "add";

	private static String UPDATE = "update";

	/**
	 * ͬ�������վ������Ϣ(ҵ����)
	 * @param house
	 * @throws Exception
	 */
	public static void addHouseToWeb(HouseBean house) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		synchronized(ADD)
		{
			try{
				conn = getConnection();

				//ͬ���ֶ����
				int index = 1;

				/**
				 * ����OA��ҵ��Guid����վ����ӵ�ҵ��GuidΪitemId�������վ��Ա��Ϣ
				 
				String [] info = getMemberInfo(house.getYzGuid());
				if(info[0]==null && info[1]==null && info[2]==null){
					throw new Exception("ҵ��["+house.getYzGuid()+"]��Ϣδͬ����");
				}
				*/
				
				ps = conn.prepareStatement(sql);
				ps.setInt(index++, house.getAreaid());
				ps.setString(index++, house.getFjmc());
				ps.setDouble(index++, house.getPrice());
				ps.setString(index++, house.getUrl1());
				ps.setString(index++, house.getUrl2());
				ps.setString(index++, house.getUrl3());
				ps.setString(index++, house.getFjpz());
				ps.setString(index++, house.getGuid());
				ps.setInt(index++, house.getFjfl());
				ps.setString(index++, house.getFptg());
				ps.setInt(index++, Common.getTimeStamp(house.getSqsj()));
				ps.setString(index++, house.getSqsj().substring(0,10));

				ps.setString(index++, house.getHuayuan());     //��԰
				ps.setString(index++, house.getQtmc());        //�Ƶ�ͷ�
				ps.setString(index++, house.getFangxing());    //�Ƶ�����
				ps.setString(index++, house.getFangwudong());  //���ݶ���Ԫ
				ps.setString(index++, house.getLouceng());     //����¥��
				ps.setString(index++, house.getFangjianhao());     //����

				ps.setString(index++, house.getHjjs());
				ps.setString(index++, house.getYljs());
				ps.setString(index++, house.getJtjs());
				ps.setString(index++, house.getGwjs());
				ps.setString(index++, house.getJdsljs());
				ps.setString(index++, "");                                        //��Ա
				ps.setInt(index++, 0);                                            //��Ա��ID
				ps.setString(index++, "");                                        //��˾
				ps.setInt(index++, Common.getTimeStamp(house.getSqsj()));
				ps.setString(index++, house.getFx());
				ps.setInt(index++, house.getMj());
				ps.setInt(index++, house.getBedNo());
				ps.setDouble(index++, house.getDiscount());
				ps.setString(index++, house.getHomeaddress());
				ps.setInt(index++, house.getNumber());
				ps.setInt(index++, 0);    //setmodel
				ps.setInt(index++, 0);    //level
				ps.setString(index++, house.getFangshu());
				ps.setString(index++, house.getHtlb());
				ps.setDouble(index++, house.getFcbl());                                                //�ֳɱ���
				ps.setString(index++, house.getBedtype());                                             //����
				ps.setString(index++, house.getJingguan());                                            //����
				ps.setString(index++, house.getArea());                                                //��
				ps.setString(index, house.getFxGuid());                                                //����Guid
				ps.executeUpdate();

				OtherInfo(house.getGuid());

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

	/**
	 * ���ݷ���GUIDͬ�����ݣ�����ģ�棬�������������������
	 * @param houseGuid
	 * @param isModel    �Ƿ���ģ�棨1�ǣ�0��
	 * @throws Exception
	 */
	public static void addHouseToWeb(String houseGuid,boolean isModel) throws Exception {
		Connection conn0 = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		synchronized(ADD)
		{
			try{
				conn = getConnection();
				conn0 = ConnectionManager.getInstance().getConnection();

				//ͬ���ֶ����
				int index = 1;

				/**
				 * ��ѯ�������
				 */
				ps = conn0.prepareStatement(querySql);
				ps.setString(1, houseGuid);
				rs = ps.executeQuery();

				/**
				 * ��Ա����
				 */
				String userName = "";
				int userId = 0;
				String company = "";

				if(rs.next()){

					/**
					String yzGuid = rs.getString("yzGuid");   //ҵ��Guid
					if(yzGuid!=null){
						String [] info = getMemberInfo(yzGuid);
						if(info[0]==null && info[1]==null && info[2]==null){
							throw new Exception("ҵ��["+yzGuid+"]��Ϣδͬ����");
						}
						else{
							userName = info[0];
							company =  info[1];
							userId =   Integer.parseInt(info[2]);
						}
					}
                    **/
					
					ps = conn.prepareStatement(sql);
					ps.setInt(index++, 0);
					ps.setString(index++, rs.getString("fjmc"));
					ps.setDouble(index++, rs.getDouble("price"));
					ps.setString(index++, "");
					ps.setString(index++, "");
					ps.setString(index++, "");
					ps.setString(index++, "");
					ps.setString(index++, rs.getString("guid"));
					ps.setInt(index++, rs.getInt("flid"));
					ps.setInt(index++, rs.getInt("fptg"));
					ps.setInt(index++, Common.getTimeStamp(rs.getString("sqsj")));
					ps.setString(index++, rs.getString("sqsj").substring(0,10));
					ps.setString(index++, rs.getString("xqhy"));        //��԰
					ps.setString(index++, rs.getString("mc"));          //�Ƶ�ͷ�
					ps.setString(index++, rs.getString("fangxing"));    //�Ƶ�����
					ps.setString(index++, rs.getString("dong")==null?"":rs.getString("dong"));        //���ݶ���Ԫ
					ps.setString(index++, rs.getString("louceng")==null?"":rs.getString("louceng"));     //����¥��
					ps.setString(index++, rs.getString("fjh")==null?"":rs.getString("fjh"));         //����
					ps.setString(index++, "");
					ps.setString(index++, "");
					ps.setString(index++, "");
					ps.setString(index++, "");
					ps.setString(index++, "");
					ps.setString(index++, userName);                                        //��Ա
					ps.setInt(index++, userId);                                             //��Ա��ID
					ps.setString(index++, company);                                         //��˾
					ps.setInt(index++, Common.getTimeStamp(rs.getString("sqsj")));
					ps.setString(index++, rs.getString("fx"));
					ps.setInt(index++, rs.getInt("fwmj"));
					ps.setInt(index++, rs.getInt("chuang"));
					ps.setDouble(index++, rs.getDouble("zk"));
					ps.setString(index++, rs.getString("xxdz"));
					ps.setInt(index++, rs.getInt("rsxz"));
					ps.setInt(index++, isModel?1:0);                                    //�Ƿ�ģ��
					ps.setInt(index++, isModel?1:0);                                    //����1��ģ��
					ps.setString(index++, cf.getNameValueByCode("a","fwmb",rs.getString("fwzt")));         //����
					String htms = rs.getString("htms");
					ps.setString(index++, htms==null?"":cf.getNameValueByCode("a","htms",htms));           //��ͬģʽ
					ps.setDouble(index++, rs.getDouble("fcbl"));                                           //�ֳɱ���
					ps.setString(index++, rs.getString("jc"));                                             //����
					ps.setString(index++, rs.getString("jg"));                                               //����
					ps.setString(index++, rs.getString("qu"));                                                //��
					ps.setString(index, rs.getString("fxGuid"));                                                //����Guid
					ps.executeUpdate(); 

				}

				OtherInfo(houseGuid);

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			finally
			{
				ConnectionManager.close(conn);
				ConnectionManager.close(conn0);
			}
		}
	}

	/**
	 * ͬ���޸���վ������Ϣ��ҵ������
	 * @param house
	 * @throws Exception
	 */
	public static void UpdateHouseToWeb(HouseBean house) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		synchronized(UPDATE)
		{
			try{
				conn = getConnection();
				//ͬ���ֶ����
				int index = 1;

				/**
				 * ����OA��ҵ��Guid����վ����ӵ�ҵ��GuidΪitemId�������վ��Ա��Ϣ
				 
				String [] info = getMemberInfo(house.getYzGuid());
				if(info[0]==null && info[1]==null && info[2]==null){
					throw new Exception("ҵ��["+house.getYzGuid()+"]��Ϣδͬ����");
				}
                */
				
				ps = conn.prepareStatement(uSql);
				ps.setInt(index++, house.getAreaid());
				ps.setString(index++, house.getFjmc());
				ps.setDouble(index++, house.getPrice());
				ps.setInt(index++, house.getFjfl());
				ps.setString(index++, house.getFptg());
				ps.setInt(index++, Common.getTimeStamp(new Timestamp(new Date().getTime()).toString()));
				ps.setString(index++, house.getFx());
				ps.setInt(index++, house.getMj());
				ps.setInt(index++, house.getBedNo());
				ps.setString(index++, "");                                        //��Ա
				ps.setString(index++, "");                                        //��˾
				ps.setInt(index++, 0);                                            //��Ա��ID
				ps.setDouble(index++, house.getDiscount());
				ps.setString(index++, house.getHomeaddress());
				ps.setInt(index++, house.getNumber());

				ps.setString(index++, house.getHuayuan());         //��԰
				ps.setString(index++, house.getQtmc());            //�Ƶ�ͷ�
				ps.setString(index++, house.getFangxing());        //����
				ps.setString(index++, house.getFangwudong());      //���ݶ���Ԫ
				ps.setString(index++, house.getLouceng());         //¥��
				ps.setString(index++, house.getFangjianhao());     //����
				ps.setString(index++, house.getHtlb());            //��ͬ���
				ps.setDouble(index++, house.getFcbl());            //�ֳɱ���
				ps.setString(index++, house.getBedtype());                                               //����
				ps.setString(index++, house.getJingguan());                                              //����
				ps.setString(index++, house.getArea());                                                //��
				ps.setString(index++, house.getFxGuid());                                                //����Guid
				ps.setString(index, house.getGuid());
				ps.executeUpdate();

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

	/**
	 * ���ݷ���GUID�޸ķ�����Ϣ������ģ�棬��������
	 * @param houseGuid
	 * @throws Exception
	 */
	public static void UpdateHouseToWeb(String houseGuid) throws Exception {
		Connection conn = null;
		Connection conn0 = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		synchronized(UPDATE)
		{
			try{
				conn0 = ConnectionManager.getInstance().getConnection();
				conn = getConnection();
				//ͬ���ֶ����
				int index = 1;

				/**
				 * ��ѯ�������
				 */
				ps = conn0.prepareStatement(querySql);
				ps.setString(1, houseGuid);
				rs = ps.executeQuery();
				if(rs.next()){
					ps = conn.prepareStatement(uSql);
					ps.setInt(index++, 0);
					ps.setString(index++, rs.getString("fjmc"));
					ps.setDouble(index++, rs.getDouble("price"));
					ps.setInt(index++, rs.getInt("flid"));
					ps.setInt(index++, rs.getInt("fptg"));
					ps.setInt(index++, Common.getTimeStamp(new Timestamp(new Date().getTime()).toString()));
					ps.setString(index++, rs.getString("fx"));
					ps.setInt(index++, rs.getInt("fwmj"));
					ps.setInt(index++, rs.getInt("chuang"));
					ps.setString(index++, "");                                        //��Ա
					ps.setString(index++, "");                                        //��˾
					ps.setInt(index++, 0);                                            //��Ա��ID
					ps.setDouble(index++, rs.getDouble("zk"));
					ps.setString(index++, rs.getString("xxdz"));
					ps.setInt(index++, rs.getInt("rsxz"));
					ps.setString(index++, rs.getString("xqhy"));                                      //��԰
					ps.setString(index++, rs.getString("mc"));                                        //�Ƶ�ͷ�
					ps.setString(index++, rs.getString("fangxing"));                                  //����
					ps.setString(index++, rs.getString("dong")==null?"":rs.getString("dong"));        //���ݶ���Ԫ
					ps.setString(index++, rs.getString("louceng")==null?"":rs.getString("louceng"));  //����¥��
					ps.setString(index++, rs.getString("fjh")==null?"":rs.getString("fjh"));          //����
					String htms = rs.getString("htms");
					ps.setString(index++, htms==null?"":cf.getNameValueByCode("a","htms",rs.getString("htms")));    //��ͬ���
					ps.setDouble(index++, rs.getDouble("fcbl"));                                      //�ֳɱ���
					ps.setString(index++, rs.getString("jc"));                                        //����
					ps.setString(index++, rs.getString("jg"));                                        //����
					ps.setString(index++, rs.getString("qu"));                                        //��
					ps.setString(index++, rs.getString("fxGuid"));                                    //����Guid
					ps.setString(index, rs.getString("guid"));
					ps.executeUpdate();

				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			finally
			{
				ConnectionManager.close(conn);
				ConnectionManager.close(conn0);
			}
		}
	}

	/**
	 * ������Ϣ
	 * @param guid
	 * @throws Exception
	 */
	public static void OtherInfo(String guid)throws Exception{
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		int itemid = 0;

		try{
			String sql = "SELECT itemid from ht_mall where guid = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, guid);
			rs = ps.executeQuery();
			if(rs.next()){
				itemid = rs.getInt(1);
			}

			//�޸����ӵ�ַ
			sql = "update ht_mall set linkurl=? where itemid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, "show.php?itemid="+itemid+"");
			ps.setInt(2, itemid);
			ps.executeUpdate();

			//�������,��ɾ�����
			sql = "INSERT INTO ht_mall_data (itemid,content) VALUES(?,?)";
			ps = conn.prepareStatement(sql);
			ps.setInt(1,itemid);
			ps.setString(2, "");
			ps.executeUpdate();
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

	/**
	 * ����վ��ȡ��Ա��Ϣ
	 * @param id
	 * @return username,company,groupid
	 * @throws Exception
	 */
	public static String[] getMemberInfo(String id)throws Exception{
		String [] info = new String[3];
		Connection conn = getConnection();
		try{
			PreparedStatement ps = null;
			ResultSet rs = null;
			String sql = "select username,company,groupid from ht_member where userid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();
			if(rs.next()){
				info[0]=rs.getString(1);
				info[1]=rs.getString(2);
				info[2]=rs.getString(3);
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
		return info;
	}

	/**
	 * ���ҵ�����Զ��ж��޸Ļ�������
	 * @param house
	 * @throws Exception
	 */
	public static void addOrUpdateHouse(HouseBean house) throws Exception {
		Connection conn = null;
		ResultSet rs = null;

		try{
			conn = getConnection();

			rs = conn.createStatement().executeQuery("select count(*) from ht_mall where guid='"+house.getGuid()+"'");

			//�м�¼ ���޸� ��������
			if(rs.next() && rs.getInt(1)>0){
				UpdateHouseToWeb(house);
			}
			else{
				addHouseToWeb(house);
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

	/**
	 * ���ݷ���Guid �ۼ�ҵ��������
	 * @param fxGuid ����Guid
	 * @throws Exception
	 */
	public static void modifyYzfNum(String fxGuid)throws Exception{
		Connection conn = ConnectionManager.getInstance().getConnection();
		String sql = "update fangxing set yzfNum = ifNull(yzfNum,0)+1 where guid=?";
		PreparedStatement ps = null;
		ps = conn.prepareStatement(sql);
		ps.executeUpdate();
	}

	/**
	 * ���ݷ���Guid �ۼ�����������
	 * @param fxGuid ����Guid
	 * @throws Exception
	 */
	public static void modifyWzfNum(String fxGuid)throws Exception{
		Connection conn = ConnectionManager.getInstance().getConnection();
		String sql = "update fangxing set wzfNum = ifNull(wzfNum,0)+1 where guid=?";
		PreparedStatement ps = null;
		ps = conn.prepareStatement(sql);
		ps.executeUpdate();
	}

	/**
	 * ����Guid�޸�WEB����״̬
	 * @param status
	 * @param guid
	 * @throws Exception
	 */
	public static void updateHouseStatus(String status,String guid)throws Exception{
		/**
		3 ͨ��
		2 ����
		1 �ܾ�
		4 �¼�
		0 ɾ��(����վ)
		 **/

		Connection conn = getConnection();
		Connection conn0 = ConnectionManager.getInstance().getConnection();
		PreparedStatement ps = null;

		String sql0 = "update house set gzlzt = ? where guid=?";
		ps = conn0.prepareStatement(sql0);
		ps.setInt(1, Integer.parseInt(status));
		ps.setString(2, guid);
		ps.executeUpdate();

		String sql = "update ht_mall set status = ? where guid=?";
		ps = conn.prepareStatement(sql);
		ps.setInt(1, Integer.parseInt(status));
		ps.setString(2, guid);
		ps.executeUpdate();
	}


	/**
	 * ��վ���Ӷ���
	 * @return
	 * @throws Exception
	 */
	private static Connection getConnection()throws Exception{
		Connection conn = ConnectionManager.getInstance().getConnection("homtrip.xml");
		return conn;
	}
}
