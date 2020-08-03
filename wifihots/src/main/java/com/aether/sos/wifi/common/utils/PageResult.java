package com.aether.sos.wifi.common.utils;

import com.aether.sos.wifi.common.finals.PubFinals;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Copyright (C) 2015 Asiainfo-Linkage
 *
 *
 * @className:com.pub.util.PageResult
 * @description:分页结果集
 * 
 * @version:v1.0.0 
 * @author:huanjun
 * 
 * Modification History:
 * Date         Author      Version     Description
 * -----------------------------------------------------------------
 * 2015年6月6日     huanjun       v1.0.0        create
 *
 *
 */
public class PageResult<T> extends ArrayList<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**分页结果集*/
	private List<T> result;
	/**总数*/
	private Long total;
	
	private int pageCount;
	
	private int currentPage;
	
	private int pageNow;
	
	private int pagePrev;
	
	private int pageNext;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getPageNow() {
		return pageNow;
	}

	public int getPagePrev() {
		return pagePrev;
	}

	public int getPageNext() {
		return pageNext;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public void setPageNow(int pageNow) {
		this.pageNow = pageNow;
	}

	public void setPagePrev(int pagePrev) {
		this.pagePrev = pagePrev;
	}

	public void setPageNext(int pageNext) {
		this.pageNext = pageNext;
	}

	public void setCurrentPage(int currentPage){
		this.currentPage = currentPage;
	}


	
	/**
	 * @param result
	 * @param total
	 */
	public PageResult(List<T> result, Long total, int pageSize) {
		super();
		this.result = result;
		this.total = total;
		this.pageCount = (int) Math.ceil((double) total / (pageSize==0? PubFinals.PAGE_SIZE_DEFAULT:pageSize));
	}
	/**getter and setter*/
	/**
	 * @return the result
	 */
	public List<T> getResult(){
		return result;
	}
	/**
	 * @return the total
	 */
	public Long getTotal() {
		return total;
	}
	
	public int getPageCount() {
		return pageCount;
	}
	
	public Map<String, Object> getPageData(){
		HashMap<String, Object> pageData = new HashMap<String, Object>();
		pageData.put(PubFinals.PAGE_DATA, this.result);
   	    pageData.put(PubFinals.PAGE_TOTAL, this.total);
   	    /*pageData.put(PubFinals.PAGE_COUNT, this.pageCount);
   	    pageData.put(PubFinals.CURRENT_PAGE, this.currentPage);
   	    pageData.put(PubFinals.PAGE_NOW, this.pageNow);
   	    pageData.put(PubFinals.PAGE_PREV, this.pagePrev);
   	    pageData.put(PubFinals.PAGE_NEXT, this.pageNext);*/
   	    return pageData;
	}
	
	public Map<String, Object> getDataWithPage(HttpServletRequest request){
		
		int pageNo = 0;
		String pn = request.getParameter("pageIndex");
		if(!StringUtils.isEmpty(pn)){
			pageNo= Integer.parseInt(pn);
		}
	        
        int currentPage=pageNo;
        
        int pagePrev = (pageNo > 1) ? (pageNo-1) : 0;
        int pageNext = (pageNo < (this.pageCount-1)) ? (pageNo + 1) : (this.pageCount-1);
        
        this.setCurrentPage(currentPage);
        this.setPageNow(pageNo);
        this.setPageNext(pageNext);
        this.setPagePrev(pagePrev);
        
        Map<String, Object> pageData = getPageData();
        return pageData;
	}
}
