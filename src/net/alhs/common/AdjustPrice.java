package net.alhs.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sysmain.common.ConnectionManager;
import net.sysmain.util.GUID;

/**
 * 调价相关接口
 * @author Administrator
 *
 */
public class AdjustPrice {

	private static final String priceType [] = new String[]{"djr","xjr","wjm","wjt","djm","djt","zr","gzr"};

	/**
	 * 根据房型模版初始化价格
	 * @param houseGuid
	 * @param price
	 * @throws Exception
	 */
	public static void initPrice(String houseGuid,double [] price)throws Exception{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try{
			conn = ConnectionManager.getInstance().getConnection();
			
			/**
			 * 查询房型所关联的房屋
			 */
			String sql = "select guid from house where fxGuid =(select fxGuid from house where guid = '"+houseGuid+"') and fwzt <> '2'";
			ps = conn.prepareStatement(sql);
			
			String iSql = "insert into fwjg(guid,houseGuid,"+priceType[0]+","+priceType[1]+","+priceType[2]+","+priceType[3]+","+priceType[4]+","+priceType[5]+","+priceType[6]+","+priceType[7]+") values(?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement ps0 = conn.prepareStatement(iSql);
			
			rs = ps.executeQuery();
			while(rs.next()){
				String guid = rs.getString("guid");
				ps0.setString(1, new GUID().toString());
				ps0.setString(2, guid);
				ps0.setDouble(3, price[0]);
				ps0.setDouble(4, price[1]);
				ps0.setDouble(5, price[2]);
				ps0.setDouble(6, price[3]);
				ps0.setDouble(7, price[4]);
				ps0.setDouble(8, price[5]);
				ps0.setDouble(9, price[6]);
				ps0.setDouble(10,price[7]);
				ps0.addBatch();
			}
			ps0.executeBatch();
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
	 * 根据调价申请单调价
	 * @param map 键：房型GUID 值：double []
	 * @throws Exception
	 */
	public static void adjustPrice(HashMap map)throws Exception{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try{
			conn = ConnectionManager.getInstance().getConnection();
			
			String sql = "select guid from house where fxGuid=?";
			ps = conn.prepareStatement(sql);

			String uSql = "update fwjg set "+priceType[0]+"=?,"+priceType[1]+"=?,"+priceType[2]+"=?,"+priceType[3]+"=?,"+priceType[4]+"=?,"+priceType[5]+"=?,"+priceType[6]+"=?,"+priceType[7]+"=? where houseGuid=?";
			PreparedStatement ps0 = conn.prepareStatement(uSql);

			Iterator it = map.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry pairs = (Map.Entry)it.next();
				
				String fxGuid = (String)pairs.getKey();
				
				double [] price = (double [])pairs.getValue();
				
				ps.setString(1, fxGuid);
				rs = ps.executeQuery();
				while(rs.next()){
					String houseGuid = rs.getString("guid");
					ps0.setDouble(1, price[0]);
					ps0.setDouble(2, price[1]);
					ps0.setDouble(3, price[2]);
					ps0.setDouble(4, price[3]);
					ps0.setDouble(5, price[4]);
					ps0.setDouble(6, price[5]);
					ps0.setDouble(7, price[6]);
					ps0.setDouble(8, price[7]);
					ps0.setString(9, houseGuid);
					ps0.addBatch();
				}
			}
			ps0.executeBatch();
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
