package com.homtrip.model;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
public class BaseEntity  
{     
    public String toString()  
   {  
        Field[] fields = this.getClass().getDeclaredFields();  
        StringBuffer strBuf = new StringBuffer();  
        for(int i=0; i<fields.length; i++)  
        {  
            String fieldName = fields[i].getName();  
           String methodName = "get" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);  
            try  
            {  
               Method method = this.getClass().getMethod(methodName, new Class[]{});  
                Object value = method.invoke(this, new Object[]{});  
                strBuf.append(fieldName + ":");  
                strBuf.append(value + "  ");  
            }  
            catch (SecurityException e)  
            {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
            catch (NoSuchMethodException e)  
            {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
            catch (IllegalArgumentException e)  
            {  
               // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
            catch (IllegalAccessException e)  
            {  
               // TODO Auto-generated catch block  
                e.printStackTrace();  
           }  
            catch (InvocationTargetException e)  
            {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
        }  
        return strBuf.toString();  
    }  
}  

