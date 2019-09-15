package com.aether.common.utils;

import java.util.List;

/**   
 * @Title: Page.java 
 * @Description: TODO
 * @author wmd 
 * @date 2015年6月5日 上午11:26:33 
 * @version V2.0   
 */
public class Page<T> {
	private List<T> data;
	private Long total;
	
	public Page(){
	}
	
	public Page(List<T> data, Long total){
		this.data = data;
		this.total = total;
	}
	
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "Page [data=" + data + ", total=" + total + "]";
	}
		
	
	
	
}
