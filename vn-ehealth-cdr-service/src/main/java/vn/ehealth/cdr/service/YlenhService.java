package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.DonThuocChiTiet;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.model.Ylenh;
import vn.ehealth.cdr.repository.YlenhRepository;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DULIEU;

@Service
public class YlenhService {

    @Autowired private YlenhRepository ylenhRepository;
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
    
    public List<Ylenh> search(ObjectId hoSoBenhAnId, String maLoaiYlenh,Date ngayBatDau, Date ngayKetThuc, int start, int count){
    	
    	var sort = new Sort(Sort.Direction.ASC, "id");
    	
    	var query = new Query(Criteria.where("hoSoBenhAnRef.objectId").is(hoSoBenhAnId)
                .and("ngayRaYlenh").gt(ngayBatDau)
                .and("ngayRaYlenh").lt(ngayKetThuc))
    			.with(sort);
    	
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
        }
        
        ylenh.hoSoBenhAnRef = HoSoBenhAn.toEmrRef(hsba);
        ylenh.benhNhanRef = hsba.benhNhanRef;
        ylenh.coSoKhamBenhRef = hsba.coSoKhamBenhRef;
        return ylenhRepository.save(ylenh);
    }
}
