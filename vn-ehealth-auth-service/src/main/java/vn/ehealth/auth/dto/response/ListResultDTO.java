package vn.ehealth.auth.dto.response;

import java.util.List;

public class ListResultDTO<T> {
    private List<T> listData;
    private Long totalRow = 0L;
    private Integer totalPage = 0;
    
    public ListResultDTO() {
    	
    }
    
    public ListResultDTO(List<T> listData, Long totalRow, Integer totalPage) {
    	this.listData = listData;
    	this.totalRow = totalRow;
    	this.totalPage = totalPage;
    }
    
	public List<T> getListData() {
		return listData;
	}
	
	public void setListData(List<T> listData) {
		this.listData = listData;
	}
	
	public Long getTotalRow() {
		return totalRow;
	}
	
	public void setTotalRow(Long totalRow) {
		this.totalRow = totalRow;
	}
	
	public Integer getTotalPage() {
		return totalPage;
	}
	
	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}
        
}