package com.aether.common.utils;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class PageUtils {
	
	public static void wrapResult(HttpServletRequest request, PageResult<Map<String, Object>> result){
		
		int pageNo=1;
		String pn = request.getParameter("pageIndex");
		if(!StringUtils.isEmpty(pn)){
			pageNo= Integer.parseInt(pn);
		}
	        
        int currentPage=pageNo;
        
        int pagePrev = pageNo>1?pageNo-1:1;
        int pageNext = pageNo<result.getPageCount()?pageNo+1:result.getPageCount();
        
        result.setCurrentPage(currentPage);
        result.setPageNow(pageNo);
        result.setPageNext(pageNext);
        result.setPagePrev(pagePrev);
	}
}
