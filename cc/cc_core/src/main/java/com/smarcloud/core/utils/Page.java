package com.smarcloud.core.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 缓存分页
 * @创建人 PengBo
 * @创建时间 2013-7-7  下午7:04:25
 */
public class Page<T> implements Serializable {

	private static final long serialVersionUID = 8968222959906061848L;
	
	private List<T> content = new ArrayList<T>();
	private Integer page = 1;   //当前页
	private Integer pageSize = 20; //分页数
	private Integer totalRow = 0; //总计路数
	private Integer totalPage= 1;// 总页数
	
	public Page() {}
	
	public Page(List<T> list, Integer page, Integer pageSize, Integer totalRow, Integer totalPage) {
		this.content = list;
		this.page = page;
		this.pageSize = pageSize;
		this.totalRow = totalRow;
		this.totalPage = totalPage;
	}
	
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getTotalRow() {
		return totalRow;
	}
	public void setTotalRow(Integer totalRow) {
		this.totalRow = totalRow;
	}
	
	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public Integer getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

}
