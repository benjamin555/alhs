package net.alhs.common;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import net.sysmain.common.ConnectionManager;

/**
 * 文本消息的模板
 * @author Owner
 *
 */
public class MsgTemplate implements Serializable 
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private static HashMap templates = new HashMap();
	
	/**
	 * 初始化
	 *
	 */
	private synchronized static void init()
	{
		Connection conn = null;
		
		try
		{
			conn = ConnectionManager.getInstance().getConnection();
			String sql = "Select Id,Content from MsgTemplate where status=1";
			
			int pos = 0;
			int pos1 = -1;
			ResultSet rs = conn.createStatement().executeQuery(sql);			
			boolean isMatch = false;
			while(rs.next())
			{
				isMatch = false;
				pos = 0;
				pos1 = -1;
				String content = rs.getString("Content");
				int id = rs.getInt("Id");
				RToken root = null;
				RToken currentToken = null;
				while(true)
				{
					if(!isMatch)
					{
						pos1 = content.indexOf("{#", pos);
						if(pos1 > 0)
						{
							RToken token = new RToken();
							token.content = content.substring(pos, pos1);
							if(currentToken != null) currentToken.nextToken = token;
							currentToken = token;
							isMatch = true;
							pos = pos1;
							if(root == null) 
							{
								root = token;
							}
						}
						else
						{//结束了
							if(content.length() > pos)
							{
								RToken token = new RToken();
								token.content = content.substring(pos);
								if(currentToken != null) currentToken.nextToken = token;
								currentToken = token;
								break;
							}
						}
					}
					else
					{
						pos1 = content.indexOf("#}", pos);
						if(pos1 > 0)
						{
							RToken token = new RToken();
							token.type = 2;
							pos = pos1+2;
							if(currentToken != null) currentToken.nextToken = token;
							currentToken = token;
							isMatch = false;
						}
						else
						{//结束了
							if(content.length() > pos)
							{
								RToken token = new RToken();
								token.content = content.substring(pos);
								if(currentToken != null) currentToken.nextToken = token;
								currentToken = token;
								break;
							}
						}
					}
				}
				if(root != null) templates.put(new Integer(id), root);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			ConnectionManager.close(conn);
		}
	}
	
	public synchronized static void refresh()
	{
		if(templates.size() > 0) templates.clear();
	}
	
	/**
	 * 依据指定模板、和值
	 * @param id
	 * @param values
	 * @return
	 */
	public static String getContent(int id, String[] values)
	{
		if(templates.size() == 0) init();
		RToken token = (RToken)templates.get(new Integer(id));
		
		if(token == null) return null;
		int x=0;
		StringBuffer bufContent = new StringBuffer();
		while(token != null)
		{
			if(token.type == 1)
			{//模板中的内容
				bufContent.append(token.content);
			}
			else if(x <values.length)
			{//顺序替换的部分
				bufContent.append(values[x++]);
			}
			token = token.nextToken;
		}
		
		return bufContent.toString();
	}
}
