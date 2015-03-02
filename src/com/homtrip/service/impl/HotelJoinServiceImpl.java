package com.homtrip.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.homtrip.common.Page;
import com.homtrip.dao.IHotelJoinDao;
import com.homtrip.model.HotelJoinVO;
import com.homtrip.service.IHotelJoinService;

@Service
@Transactional(readOnly=true)
public class HotelJoinServiceImpl implements IHotelJoinService  {
	
	    @Resource
	    private IHotelJoinDao hotelDao;  
	   
	    
	    @Override
	    public boolean delete(int id)  
	    {  
	        // TODO Auto-generated method stub  
	       return hotelDao.delete(id);  
	    }  
	    
	    @Override
	    public List<HotelJoinVO> findAll()  
	    {  
	        // TODO Auto-generated method stub  
	        return hotelDao.findAll();  
	    }  
	    @Override
	    @Transactional
	    public boolean insert(HotelJoinVO stu)  
	    {  
    	    hotelDao.insert(stu);  
	        return true;  
	    }  
	    
	    @Override
	    @Transactional
	    public boolean update(HotelJoinVO stu)  
       {  
	        // TODO Auto-generated method stub  
	       return hotelDao.update(stu);  
	    }

		@Override
		public Page<HotelJoinVO> getPage(int start, int size, Map<String, String> searchMap) {
			return hotelDao.getPage(start, size, searchMap);
		}

		@Override
		public HotelJoinVO findByGuid(String guid) {
			return hotelDao.findByGuid(guid);
		}
		
		@Transactional
		@Override
		public boolean updatestatus(String guid) {
			// TODO Auto-generated method stub
			return hotelDao.updatestatus(guid);
		}  

}
