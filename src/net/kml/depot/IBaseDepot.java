package net.kml.depot;

import java.sql.Connection;

public interface IBaseDepot {
	
	public boolean inMaterials(DepotRecordBean depot,Connection conn) throws Exception;

	public boolean outMaterialst(DepotRecordBean depot,Connection conn) throws Exception;

	public boolean inProduct(DepotRecordBean depot,Connection conn) throws Exception;

	public boolean outProduct(DepotRecordBean depot,Connection conn) throws Exception;
	
	public boolean avgPrice(String guid,Connection conn) throws Exception;
	
}
