package vn.ehealth.emr.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class OffsetBasedPageRequest implements Pageable {
    private int limit;
    private int offset;
    private Sort sort;
    
    public OffsetBasedPageRequest(int limit, int offset, Sort sort) {
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must not be less than one!");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset index must not be less than zero!");
        }
        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
    }
    
    @Override
    public int getPageNumber() {
        return offset / limit;
    }
    
    @Override
    public int getPageSize() {
        return limit;
    }
    
    @Override
    public long getOffset() {
        return offset;
    }
    
    @Override
    public Sort getSort() {
        return sort;
    }
    
    @Override
    public Pageable next() {
        return new OffsetBasedPageRequest(getPageSize(), (int) (getOffset() + getPageSize()), sort);
    }
    
    public Pageable previous() {
        return hasPrevious() ?
                new OffsetBasedPageRequest(getPageSize(), (int) (getOffset() - getPageSize()), sort): this;
    }
    
    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }
    
    @Override
    public Pageable first() {
        return new OffsetBasedPageRequest(0, getPageSize(), sort);
    }
    
    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }
}