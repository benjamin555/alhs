package net.alhs.api;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;

import net.business.db.CodeFactory;
import net.sysmain.common.ConnectionManager;
/**
 * 房间接口类
 * @author Administrator
 *
 */
public class HouseAction{

	private static CodeFactory cf = new CodeFactory();

	/**
	 * 新增SQL
	 */
	private static final String sql = "INSERT INTO ht_mall(areaid,title,price,thumb,thumb1,thumb2,homedevice,guid,catid,hometicket,addtime,adddate," +
	"garden,hotelkefu,hoteltype,building,ground,roomno," +
	"relate_name,relate_id,relate_title,n1,n2,n3,v1,v2,v3,express_name_1,fee_start_1,fee_step_1,express_name_2,fee_start_2,fee_step_2,express_name_3,fee_start_3,fee_step_3,homearea," +
	"huanjing,yule,traffic,shopping,trippoint,username,groupid,company,validated,status,edittime,editdate," +
	"roomtype,roomarea,bedno,imageid,xuzhi,discount,homeaddress,number,couldorder,setmodel,level,datedata,fangshu,bargaintype,divide,bedtype,jingguan,city,modeid) " +
	"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
	"'','','','','','','','','','',0.00,0.00,'',0.00,0.00,'',0.00,0.00,0,?,?,?,?,?,?,?,?,1,3,?,now(),?,?,?,0,'',?,?,?,'',?,?,'',?,?,?,?,?,?,?)";

	/**
	 * 修改SQL
	 */
	private static final String uSql = "update ht_mall set areaid=?,title=?,price=?,catid=?,hometicket=?,edittime=?,editdate=now(),roomtype=?,roomarea=?,bedno=?,username=?,company=?,groupid=?,discount=?,homeaddress=?,number=? " +
	",garden=?,hotelkefu=?,hoteltype=?,building=?,ground=?,roomno=?,bargaintype=?,divide=?,bedtype=?,jingguan=?,city=?,modeid=? where guid=?";

	/**
	 * 查询房屋SQL
	 */
	private static final String querySql = "SELECT h.*,q.mc,f.chuang,f.jc,f.jg FROM house h,qtjzx q,fwwlxx f WHERE h.qtGuid=q.guid  AND h.guid = f.houseGuid and h.guid=?";

	private static String ADD = "add";

	private static String UPDATE = "update";

	/**
	 * 同步添加网站房屋信息(业主房)
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

				//同步字段序号
				int index = 1;

				/**
				 * 根据OA中业主Guid（网站中添加的业主Guid为itemId）查出网站会员信息
				 
				String [] info = getMemberInfo(house.getYzGuid());
				if(info[0]==null && info[1]==null && info[2]==null){
					throw new Exception("业主["+house.getYzGuid()+"]信息未同步！");
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

				ps.setString(index++, house.getHuayuan());     //花园
				ps.setString(index++, house.getQtmc());        //酒店客服
				ps.setString(index++, house.getFangxing());    //酒店类型
				ps.setString(index++, house.getFangwudong());  //房屋栋单元
				ps.setString(index++, house.getLouceng());     //房屋楼层
				ps.setString(index++, house.getFangjianhao());     //房号

				ps.setString(index++, house.getHjjs());
				ps.setString(index++, house.getYljs());
				ps.setString(index++, house.getJtjs());
				ps.setString(index++, house.getGwjs());
				ps.setString(index++, house.getJdsljs());
				ps.setString(index++, "");                                        //会员
				ps.setInt(index++, 0);                                            //会员组ID
				ps.setString(index++, "");                                        //公司
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
				ps.setDouble(index++, house.getFcbl());                                                //分成比率
				ps.setString(index++, house.getBedtype());                                             //床型
				ps.setString(index++, house.getJingguan());                                            //景观
				ps.setString(index++, house.getArea());                                                //区
				ps.setString(index, house.getFxGuid());                                                //房型Guid
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
	 * 根据房间GUID同步房屋（房型模版，无主房，另存无主房）
	 * @param houseGuid
	 * @param isModel    是否是模版（1是，0否）
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

				//同步字段序号
				int index = 1;

				/**
				 * 查询，再添加
				 */
				ps = conn0.prepareStatement(querySql);
				ps.setString(1, houseGuid);
				rs = ps.executeQuery();

				/**
				 * 会员处理
				 */
				String userName = "";
				int userId = 0;
				String company = "";

				if(rs.next()){

					/**
					String yzGuid = rs.getString("yzGuid");   //业主Guid
					if(yzGuid!=null){
						String [] info = getMemberInfo(yzGuid);
						if(info[0]==null && info[1]==null && info[2]==null){
							throw new Exception("业主["+yzGuid+"]信息未同步！");
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
					ps.setString(index++, rs.getString("xqhy"));        //花园
					ps.setString(index++, rs.getString("mc"));          //酒店客服
					ps.setString(index++, rs.getString("fangxing"));    //酒店类型
					ps.setString(index++, rs.getString("dong")==null?"":rs.getString("dong"));        //房屋栋单元
					ps.setString(index++, rs.getString("louceng")==null?"":rs.getString("louceng"));     //房屋楼层
					ps.setString(index++, rs.getString("fjh")==null?"":rs.getString("fjh"));         //房号
					ps.setString(index++, "");
					ps.setString(index++, "");
					ps.setString(index++, "");
					ps.setString(index++, "");
					ps.setString(index++, "");
					ps.setString(index++, userName);                                        //会员
					ps.setInt(index++, userId);                                             //会员组ID
					ps.setString(index++, company);                                         //公司
					ps.setInt(index++, Common.getTimeStamp(rs.getString("sqsj")));
					ps.setString(index++, rs.getString("fx"));
					ps.setInt(index++, rs.getInt("fwmj"));
					ps.setInt(index++, rs.getInt("chuang"));
					ps.setDouble(index++, rs.getDouble("zk"));
					ps.setString(index++, rs.getString("xxdz"));
					ps.setInt(index++, rs.getInt("rsxz"));
					ps.setInt(index++, isModel?1:0);                                    //是否模版
					ps.setInt(index++, isModel?1:0);                                    //级别1，模版
					ps.setString(index++, cf.getNameValueByCode("a","fwmb",rs.getString("fwzt")));         //房属
					String htms = rs.getString("htms");
					ps.setString(index++, htms==null?"":cf.getNameValueByCode("a","htms",htms));           //合同模式
					ps.setDouble(index++, rs.getDouble("fcbl"));                                           //分成比率
					ps.setString(index++, rs.getString("jc"));                                             //床型
					ps.setString(index++, rs.getString("jg"));                                               //景观
					ps.setString(index++, rs.getString("qu"));                                                //区
					ps.setString(index, rs.getString("fxGuid"));                                                //房型Guid
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
	 * 同步修改网站房屋信息（业主房）
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
				//同步字段序号
				int index = 1;

				/**
				 * 根据OA中业主Guid（网站中添加的业主Guid为itemId）查出网站会员信息
				 
				String [] info = getMemberInfo(house.getYzGuid());
				if(info[0]==null && info[1]==null && info[2]==null){
					throw new Exception("业主["+house.getYzGuid()+"]信息未同步！");
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
				ps.setString(index++, "");                                        //会员
				ps.setString(index++, "");                                        //公司
				ps.setInt(index++, 0);                                            //会员组ID
				ps.setDouble(index++, house.getDiscount());
				ps.setString(index++, house.getHomeaddress());
				ps.setInt(index++, house.getNumber());

				ps.setString(index++, house.getHuayuan());         //花园
				ps.setString(index++, house.getQtmc());            //酒店客服
				ps.setString(index++, house.getFangxing());        //房型
				ps.setString(index++, house.getFangwudong());      //房屋栋单元
				ps.setString(index++, house.getLouceng());         //楼层
				ps.setString(index++, house.getFangjianhao());     //房号
				ps.setString(index++, house.getHtlb());            //合同类别
				ps.setDouble(index++, house.getFcbl());            //分成比例
				ps.setString(index++, house.getBedtype());                                               //床型
				ps.setString(index++, house.getJingguan());                                              //景观
				ps.setString(index++, house.getArea());                                                //区
				ps.setString(index++, house.getFxGuid());                                                //房型Guid
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
	 * 根据房间GUID修改房屋信息（房型模版，无主房）
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
				//同步字段序号
				int index = 1;

				/**
				 * 查询，再添加
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
					ps.setString(index++, "");                                        //会员
					ps.setString(index++, "");                                        //公司
					ps.setInt(index++, 0);                                            //会员组ID
					ps.setDouble(index++, rs.getDouble("zk"));
					ps.setString(index++, rs.getString("xxdz"));
					ps.setInt(index++, rs.getInt("rsxz"));
					ps.setString(index++, rs.getString("xqhy"));                                      //花园
					ps.setString(index++, rs.getString("mc"));                                        //酒店客服
					ps.setString(index++, rs.getString("fangxing"));                                  //房型
					ps.setString(index++, rs.getString("dong")==null?"":rs.getString("dong"));        //房屋栋单元
					ps.setString(index++, rs.getString("louceng")==null?"":rs.getString("louceng"));  //房屋楼层
					ps.setString(index++, rs.getString("fjh")==null?"":rs.getString("fjh"));          //房号
					String htms = rs.getString("htms");
					ps.setString(index++, htms==null?"":cf.getNameValueByCode("a","htms",rs.getString("htms")));    //合同类别
					ps.setDouble(index++, rs.getDouble("fcbl"));                                      //分成比例
					ps.setString(index++, rs.getString("jc"));                                        //床型
					ps.setString(index++, rs.getString("jg"));                                        //景观
					ps.setString(index++, rs.getString("qu"));                                        //区
					ps.setString(index++, rs.getString("fxGuid"));                                    //房型Guid
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
	 * 其他信息
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

			//修改链接地址
			sql = "update ht_mall set linkurl=? where itemid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, "show.php?itemid="+itemid+"");
			ps.setInt(2, itemid);
			ps.executeUpdate();

			//房间介绍,先删再添加
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
	 * 从网站获取会员信息
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
	 * 添加业主房自动判断修改或者新增
	 * @param house
	 * @throws Exception
	 */
	public static void addOrUpdateHouse(HouseBean house) throws Exception {
		Connection conn = null;
		ResultSet rs = null;

		try{
			conn = getConnection();

			rs = conn.createStatement().executeQuery("select count(*) from ht_mall where guid='"+house.getGuid()+"'");

			//有记录 则修改 否则新增
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
	 * 根据房型Guid 累加业主房数量
	 * @param fxGuid 房型Guid
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
	 * 根据房型Guid 累加无主房数量
	 * @param fxGuid 房型Guid
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
	 * 根据Guid修改WEB房屋状态
	 * @param status
	 * @param guid
	 * @throws Exception
	 */
	public static void updateHouseStatus(String status,String guid)throws Exception{
		/**
		3 通过
		2 待审
		1 拒绝
		4 下架
		0 删除(回收站)
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
	 * 网站连接对象
	 * @return
	 * @throws Exception
	 */
	private static Connection getConnection()throws Exception{
		Connection conn = ConnectionManager.getInstance().getConnection("homtrip.xml");
		return conn;
	}
}
