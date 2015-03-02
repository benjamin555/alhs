package net.alhs.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;

import net.sysmain.util.StringTools;

public class CalendarUtil implements Serializable
{   
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public final static String[] weekName = new String[]{"<font color=red>������</font>", "����һ", "���ڶ�", "������", 
                                                         "������", "������", "<font color=blue>������</font>"}; 
    
    public static ArrayList getMonthCalendar(HttpServletRequest request)
    {
        ArrayList list = new ArrayList();
        
        /**
         * ��ǰ�ꡢ��
         */
        int year = 0;
        int month = 0; 
        
        int days = 0;
        Calendar cal = Calendar.getInstance();
        
        /**
         * ͨ���������������
         */
        if(StringTools.isInteger(request.getParameter("year")))
        {
            year = Integer.parseInt(request.getParameter("year").trim());     
            if(year <1999 || year > 9999) year = 0;
        }
        if(StringTools.isInteger(request.getParameter("month")))
        {
            month = Integer.parseInt(request.getParameter("month").trim());
            if(month <0 || month > 12) month = 0;
        }
        
        if(year == 0) year = cal.get(Calendar.YEAR);
        if(month == 0) month = cal.get(Calendar.MONTH) + 1;

        net.chysoft.util.DateUtil dateUtil = new net.chysoft.util.DateUtil();
        //��¼��ǰ�����
        request.setAttribute("month" , new Integer(month));
        request.setAttribute("year" , new Integer(year));
        
        
        StringBuffer yearStr= new StringBuffer("<select id=year onchange='changeDate();'>");
        //��
        for(int i = year-4; i <year+3; i++)
        {
            if(i==year)
            {
                yearStr.append("<option value=").append(i).append(" selected=selected>").append(i).append("</option>");
            }
            else
            {
                yearStr.append("<option value=").append(i).append(">").append(i).append("</option>");
            }
        }
        yearStr.append("</select>��");

        //��
        StringBuffer monthStr= new StringBuffer("&nbsp;<select id=month onchange='changeDate();'>");
        for(int i=1; i<13; i++)
        {
            if(i == month)
            {
                monthStr.append("<option value=").append(i).append(" selected=selected>").append(i).append("</option>");
            }
            else
            {
                monthStr.append("<option value=").append(i).append(">").append(i).append("</option>");
            }
        }
        monthStr.append("</select>��");
        
        request.setAttribute("yearSel", yearStr.toString());
        request.setAttribute("monthSel", monthStr.toString());
        
        /**
         * ����һ�����е�����
         */
        days = dateUtil.getDays(month - 1, year);

        //������һ��������ʱ���(�����յ�����һ�ֱ���  1 2 3 4 5 6 7, ��������1��
        
        
        /**
         * ���ص�ǰ���ꡢ�¡���
         */
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH)+1;
        int d = cal.get(Calendar.DAY_OF_MONTH);
        request.setAttribute("cDate", new int[]{d, m, y});

        /**
         * ����Ϊ���µ�1��
         */
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, month-1);
        //��ȡ����
        int week = cal.get(Calendar.DAY_OF_WEEK); 
        if(week == 1) week = 8;
        int weekInterVal1 = week - 2;  //��������һ������
        
        /**
         * ����Ϊ���µ����һ��
         */
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, days);
        cal.set(Calendar.MONTH, month-1);
        int week1 = cal.get(Calendar.DAY_OF_WEEK); 
        if(week1 == 1) week1 = 8;
        int weekInterVal2 = 8 - week1;  //���������յ�����
        
        /**
         * 1���ϸ���ʣ�������
         */
        if(weekInterVal1 > 0)
        {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.DAY_OF_MONTH, 1); //���³�
            cal.set(Calendar.MONTH, month-1);
            //���ص�������һ 
            cal.add(Calendar.DAY_OF_MONTH, -weekInterVal1);
            int n = year;
            int lastDay = cal.get(Calendar.DAY_OF_MONTH);
            int x = month-1;
            if(x == 0) 
            {
                x = 12;
                n = n-1;
            }
            for(int i=lastDay; i<lastDay+weekInterVal1; i++)
            {
                list.add(new int[]{i, x, n});
            }
        }
        
        /**
         * 2�����µ�����
         */
        for(int i=1; i<=days; i++)
        {
            list.add(new int[]{i, month, year});
        }
        
        /**
         * 3���¸��µ�����
         */
        if(weekInterVal2 > 0)
        {
            int x = month+1;
            int n = year;
            if(x == 13) 
            {
                x = 1;
                n = n + 1;
            }
            for(int i=1; i<=weekInterVal2; i++)
            {
                list.add(new int[]{i, x, n});
            }
        }
        
        return list;
        
    }
    
    /**
     * �����ܵ������Ϣ
     * @param request
     * @return
     */
    public static ArrayList getWeekCalendar(HttpServletRequest request)
    {   
        /**
         * ��ǰ�ꡢ��
         */
        int year = 0;
        int month = 0; 
        int day = 0;
        ArrayList list = new ArrayList();
        
        /**
         * ͨ���������������
         */
        if(StringTools.isInteger(request.getParameter("year")))
        {
            year = Integer.parseInt(request.getParameter("year").trim());     
            if(year <1999 || year > 9999) year = 0;
        }
        
        if(StringTools.isInteger(request.getParameter("month")))
        {
            month = Integer.parseInt(request.getParameter("month").trim());
            if(month <0 || month > 12) month = 0;
        }
        
        if(StringTools.isInteger(request.getParameter("day")))
        {
            day = Integer.parseInt(request.getParameter("day").trim());
            if(day <0 || day > 31) month = 0;
        }
        
        //��¼��ǰ���ꡢ�¡���
        Calendar cal = Calendar.getInstance();
        int yy = cal.get(Calendar.YEAR);
        int mm = cal.get(Calendar.MONTH) + 1;
        int dd = cal.get(Calendar.DAY_OF_MONTH);
        request.setAttribute("cDate", new int[]{yy, mm, dd});
        
        if(year == 0) year = cal.get(Calendar.YEAR);
        if(month == 0) month = cal.get(Calendar.MONTH) + 1;
        if(day == 0) day = cal.get(Calendar.DAY_OF_MONTH);
        
        
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.MONTH, month-1);
        
        int week = cal.get(Calendar.DAY_OF_WEEK); 
        if(week == 1) week = 8;
        int weekInterVal1 = week - 2;  //��������һ������
        
        int y, m , d;
        
       
        //���õ���һ
        if(weekInterVal1 > 0)
        {
            cal.add(Calendar.DAY_OF_MONTH, -weekInterVal1);
        }
        //�ж��Ƿ��ڱ���
        request.setAttribute("isCurrentWeek", isCurrentWeek(cal)?"1":"0");
        
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH) + 1;
        d = cal.get(Calendar.DAY_OF_MONTH);
        list.add(new int[]{y, m, d});
        
        //����������
        for(int i=1; i<7; i++)
        {
            //�������1��
            cal.add(Calendar.DAY_OF_MONTH, 1);
            y = cal.get(Calendar.YEAR);
            m = cal.get(Calendar.MONTH) + 1;
            d = cal.get(Calendar.DAY_OF_MONTH);
            list.add(new int[]{y, m, d});
        }
        
        //���ܵ����ݣ� ����һ
        cal.add(Calendar.DAY_OF_MONTH, 1);
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH) + 1;
        d = cal.get(Calendar.DAY_OF_MONTH);
        request.setAttribute("nextWeek", new int[]{y, m, d});
        
        //���ܵ����ݣ�����һ
        cal.add(Calendar.DAY_OF_MONTH, -14);
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH) + 1;
        d = cal.get(Calendar.DAY_OF_MONTH);
        request.setAttribute("preWeek", new int[]{y, m, d});
        
        return list;
    }
    
   /**
    * �ж��Ƿ���ͬһ��
    * @param cal0 ��һ������
    * @return
    */
    private static boolean isCurrentWeek(Calendar cal0)
    {
        Calendar cal = Calendar.getInstance();
                
        int week = cal.get(Calendar.DAY_OF_WEEK); 
        if(week == 1) week = 8;
        int weekInterVal1 = week - 2;  //��������һ������
        
        //���õ���һ
        if(weekInterVal1 > 0)
        {
            cal.add(Calendar.DAY_OF_MONTH, -weekInterVal1);
        }
        int y1 = cal.get(Calendar.YEAR);
        int m1 = cal.get(Calendar.MONTH);
        int d1 = cal.get(Calendar.DAY_OF_MONTH);
        
        int y2 = cal0.get(Calendar.YEAR);
        int m2 = cal0.get(Calendar.MONTH);
        int d2 = cal0.get(Calendar.DAY_OF_MONTH);
        
        return y1==y2 && m1==m2 && d1==d2;
    }
    
    /**
     * �����յ������Ϣ
     * @param request
     * @return
     */
    public static int[] getDayCalendar(HttpServletRequest request)
    {   
        /**
         * ��ǰ�ꡢ��
         */
        int year = 0;
        int month = 0; 
        int day = 0;
        
        /**
         * ͨ���������������
         */
        if(StringTools.isInteger(request.getParameter("year")))
        {
            year = Integer.parseInt(request.getParameter("year").trim());     
            if(year <1999 || year > 9999) year = 0;
        }
        
        if(StringTools.isInteger(request.getParameter("month")))
        {
            month = Integer.parseInt(request.getParameter("month").trim());
            if(month <0 || month > 12) month = 0;
        }
        
        if(StringTools.isInteger(request.getParameter("day")))
        {
            day = Integer.parseInt(request.getParameter("day").trim());
            if(day <0 || day > 31) month = 0;
        }
        
        Calendar cal = Calendar.getInstance();

        int yy = cal.get(Calendar.YEAR);
        int mm = cal.get(Calendar.MONTH)+1;
        int dd = cal.get(Calendar.DAY_OF_MONTH);
                
        if(year == 0) year = cal.get(Calendar.YEAR);
        if(month == 0) month = cal.get(Calendar.MONTH) + 1;
        if(day == 0) day = cal.get(Calendar.DAY_OF_MONTH);
        
        
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.MONTH, month-1);
        
        /**
         * ���ص�ǰ���ꡢ�¡��ա�����
         */
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DAY_OF_MONTH);
        int w = cal.get(Calendar.DAY_OF_WEEK)-1; 
        
        //�ж�ѡ�е��ꡢ�¡����Ƿ��ǽ���
        request.setAttribute("isToday", (yy==y && mm==m && dd == d)?"1":"0");
        
        return new int[]{y, m, d, w};
    }
    
    private StringBuffer title = new StringBuffer();
    
    public String getMonthDayHtml(int[] v, int selectedMonth, int[] cDate)
    {   
        if(title.length() > 0) title.delete(0, title.length());
        if(v[1] == selectedMonth) 
        {
            if(v[0]==cDate[0] && v[1]==cDate[1] && v[2]==cDate[2])
            {
                title.append("<b>");
                title.append(v[1]);
                title.append("��");
                title.append(v[0]);
                title.append("��</b>[<font color=\"#F05050\">����</font>]");
            }
            else
            {
                title.append(v[1]);
                title.append("��");
                title.append(v[0]);
                title.append("��");
            }
        }
        else
        {
            if(v[0]==cDate[0] && v[1]==cDate[1] && v[2]==cDate[2])
            {
                title.append("<font color=\"#C0C0C0\"><b>");
                title.append(v[1]);
                title.append("��");
                title.append(v[0]);
                title.append("��</b>[<font color=\"#F05050\">����</font>]");
            }
            else
            {
                title.append("<font color=\"#C0C0C0\">");
                title.append(v[1]);
                title.append("��");
                title.append(v[0]);
                title.append("��</font>");
           }
       }
       
       return title.toString();
    }
}
