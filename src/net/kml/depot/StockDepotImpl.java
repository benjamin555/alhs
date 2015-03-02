package net.kml.depot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
 
public class StockDepotImpl implements IBaseDepot{

	/**
     * 入库的平均价格计算
     * 
     *    最近5次总的入库价格/最近5次总的入库数量
     *    5次的价格是参与计算的
	 */
    public boolean avgPrice(String guid, Connection conn) throws Exception
    {
		PreparedStatement ps = null;
		ResultSet rs = null;
        String sql = "SELECT rprice,storageNum FROM stockStorage WHERE materialsGuid=? and IsPriceAverage='1' ORDER BY storageTime DESC LIMIT 5";
		
		ps = conn.prepareStatement(sql);
		
		ps.setString(1, guid);
		
		rs = ps.executeQuery();
		
		double avg = 0;
		
		double money = 0 ;
		
		double cusor = 0d ; //入库数量
		
		while(rs.next())
        {
			cusor = cusor + rs.getDouble("storageNum");
			money += rs.getDouble("rprice") * rs.getDouble("storageNum");
		}
		if(cusor > 0)
        {
			avg = money / cusor;  
			
			sql = "update goods set avgPrice=?,amonut=? * count where guid = ? ";
			ps = conn.prepareStatement(sql);
			ps.setDouble(1, avg);
			ps.setDouble(2, avg);
			ps.setString(3, guid);
			ps.executeUpdate(); 
		}
		return true;
	}

	public boolean inMaterials(DepotRecordBean depot, Connection conn) throws Exception {
		
		PreparedStatement ps = null;
			
		String sql = "INSERT INTO depotRecord (guid,pmName,pmModel,pmType,porm,pmGuid,outOrin,operationTime,operationNum,operationPerson,subjects,subjectsVal) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		ps = conn.prepareStatement(sql);
		ps.setString(1, depot.getGuid());
		ps.setString(2, depot.getPmName());
		ps.setString(3, depot.getPmModel());
		ps.setString(4, depot.getPmType());
		ps.setInt(5, depot.getPorm());
		ps.setString(6, depot.getPmGuid());
		ps.setInt(7, depot.getOutOrin());
		ps.setTimestamp(8, new Timestamp(depot.getOperationTime().getTime()));
		ps.setDouble(9, depot.getOperationNum());
		ps.setString(10, depot.getOperationPerson());
		ps.setInt(11, depot.getSubjects());
		ps.setString(12, depot.getSubjectsVal());
		ps.executeUpdate();
		
		sql = "update goods set monthEnterNum = ifNull(monthEnterNum,0) + ? where guid = ? ";
		ps = conn.prepareStatement(sql);
		ps.setDouble(1, depot.getOperationNum());
		ps.setString(2, depot.getPmGuid());
		ps.executeUpdate();
		return true;
	}

	public boolean inProduct(DepotRecordBean depot, Connection conn) throws Exception{
		return false;
	}

	public boolean outMaterialst(DepotRecordBean depot, Connection conn) throws Exception{
		PreparedStatement ps = null;
			
		String sql = "INSERT INTO depotRecord (guid,pmName,pmModel,pmType,porm,pmGuid,outOrin,operationTime,operationNum,operationPerson,subjects,subjectsVal) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		ps = conn.prepareStatement(sql);
		ps.setString(1, depot.getGuid());
		ps.setString(2, depot.getPmName());
		ps.setString(3, depot.getPmModel());
		ps.setString(4, depot.getPmType());
		ps.setInt(5, depot.getPorm());
		ps.setString(6, depot.getPmGuid());
		ps.setInt(7, depot.getOutOrin());
		ps.setTimestamp(8, new Timestamp(depot.getOperationTime().getTime()));
		ps.setDouble(9, depot.getOperationNum());
		ps.setString(10, depot.getOperationPerson());
		ps.setInt(11, depot.getSubjects());
		ps.setString(12, depot.getSubjectsVal());
		ps.executeUpdate();
		
		sql = "update goods set monthOutNum = ifNull(monthOutNum,0)+? where guid = ? ";
		ps = conn.prepareStatement(sql);
		ps.setDouble(1, depot.getOperationNum());
		ps.setString(2, depot.getPmGuid());
		ps.executeUpdate();
		return true; 
	}

	public boolean outProduct(DepotRecordBean depot, Connection conn) throws Exception{
		return false;
	}
}
