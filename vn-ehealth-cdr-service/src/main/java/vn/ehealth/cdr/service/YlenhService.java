package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.model.Ylenh;
import vn.ehealth.cdr.repository.YlenhRepository;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DULIEU;

@Service
public class YlenhService {

    @Autowired private YlenhRepository ylenhRepository;
    @Autowired private DonThuocService donThuocService;
    @Autowired private MongoTemplate mongoTemplate;
    
    public Optional<Ylenh> getById(ObjectId id) {
        return ylenhRepository.findById(id);
    }
    
    public List<Ylenh> getByHoSoBenhAnId(ObjectId hoSoBenhAnId, int start, int count) {
        if(start >= 0 && count >= 0) {
            var pageable = new OffsetBasedPageable(count, start, Sort.by("id"));
            return ylenhRepository.findByHoSoBenhAnRefObjectIdAndTrangThai(hoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT, pageable);
        }else {
            return ylenhRepository.findByHoSoBenhAnRefObjectIdAndTrangThai(hoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT); 
        }
    }
    
    public Criteria createCriteriaByLoaiAndNgayRaYlenh(ObjectId hoSoBenhAnId, String maLoaiYlenh,Date ngayBatDau, Date ngayKetThuc) {
        var criteria = Criteria.where("hoSoBenhAnRef.objectId").is(hoSoBenhAnId);
        
        if(ngayBatDau != null && ngayKetThuc == null) {
            criteria = criteria.and("ngayRaYlenh").gt(ngayBatDau);
        }
        
        if(ngayBatDau == null && ngayKetThuc != null) {
            criteria = criteria.and("ngayRaYlenh").lt(ngayKetThuc);
        }
        
        if(ngayBatDau != null && ngayKetThuc != null) {
            criteria = criteria.and("ngayRaYlenh").gt(ngayBatDau).lt(ngayKetThuc);
        }
        
        if(!StringUtils.isEmpty(maLoaiYlenh)) {
            criteria.and("dmLoaiYlenh.ma").is(maLoaiYlenh);
        }
        
        //return MongoUtils.createQuery(Map.of("hoSoBenhAnRef.objectId", hoSoBenhAnId));
        return criteria;
    }
    
    public long countByLoaiAndNgayRaYlenh(ObjectId hoSoBenhAnId, String maLoaiYlenh, Date ngayBatDau, Date ngayKetThuc) {
        var criteria = createCriteriaByLoaiAndNgayRaYlenh(hoSoBenhAnId, maLoaiYlenh, ngayBatDau, ngayKetThuc);
        return mongoTemplate.count(new Query(criteria), Ylenh.class);
    }
    
    public List<Ylenh> getByLoaiAndNgayRaYlenh(ObjectId hoSoBenhAnId, String maLoaiYlenh, Date ngayBatDau, Date ngayKetThuc, int start, int count){
    	var criteria = createCriteriaByLoaiAndNgayRaYlenh(hoSoBenhAnId, maLoaiYlenh, ngayBatDau, ngayKetThuc);
    	
    	var sort = new Sort(Sort.Direction.ASC, "id");
    	var query = new Query(criteria).with(sort);
    	
    	if(start >= 0 & count >= 0) {
    	    query.skip(start).limit(count);
    	}
    	
    	return mongoTemplate.find(query, Ylenh.class);
    }
    
    public List<Ylenh> getByHoSoBenhAnIdAndLoaiYlenh(ObjectId hoSoBenhAnId, String maLoaiYlenh,Date ngayBatDau, Date ngayKetThuc, int start, int count) {
        var pageable = new OffsetBasedPageable(count, start, Sort.by("id"));
        return ylenhRepository.findByHoSoBenhAnRefObjectIdAndDmLoaiYlenhMaAndTrangThai(hoSoBenhAnId, maLoaiYlenh, TRANGTHAI_DULIEU.DEFAULT, pageable);
    }
    
    public Ylenh save(@Nonnull Ylenh ylenh) {
        return ylenhRepository.save(ylenh);        
    }
    
    public Ylenh createOrUpdateFromHis(@Nonnull HoSoBenhAn hsba, @Nonnull Ylenh ylenh) {
        if(ylenh.idhis != null) {
            ylenh.id = ylenhRepository.findByIdhis(ylenh.idhis).map(x -> x.id).orElse(null);
            if(ylenh.id != null) {
                donThuocService.deleteByYlenhId(ylenh.id);
            }
        }
        
        ylenh.hoSoBenhAnRef = HoSoBenhAn.toEmrRef(hsba);
        ylenh.benhNhanRef = hsba.benhNhanRef;
        ylenh.coSoKhamBenhRef = hsba.coSoKhamBenhRef;
        return ylenhRepository.save(ylenh);
    }
}
