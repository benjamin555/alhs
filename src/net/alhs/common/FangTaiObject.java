package net.alhs.common;

public class FangTaiObject implements java.io.Serializable,Cloneable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nyr;
	private String price;
	/**
     * 预先在内存中创建一个实例，用于克隆
     */
    private static FangTaiObject _instance = new FangTaiObject();
	
    private FangTaiObject(){}
    
    public static FangTaiObject getInstance() throws Exception
    {
    	FangTaiObject _bean = (FangTaiObject)_instance.clone();
        
        return _bean;
    }
    
	public void setNyr(String nyr) 
	{
		this.nyr = nyr;
	}
	
	public String getNyr() 
	{
		return nyr;
	}

	public void setPrice(String price) 
	{
		this.price = price;
	}

	public String getPrice() 
	{
		return price;
	}
	
	public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }  
}
