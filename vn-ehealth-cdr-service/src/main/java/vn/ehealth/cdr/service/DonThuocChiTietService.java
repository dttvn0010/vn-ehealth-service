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
import vn.ehealth.cdr.repository.DonThuocChiTietRepository;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DULIEU;

@Service
public class DonThuocChiTietService {

    @Autowired private DonThuocChiTietRepository donThuocChiTietRepository;
    
    @Autowired private MongoTemplate mongoTemplate;
    
    public Optional<DonThuocChiTiet> getById(ObjectId id) {
        return donThuocChiTietRepository.findById(id);
    }
    
    public DonThuocChiTiet save(DonThuocChiTiet donThuocChiTiet) {
        return donThuocChiTietRepository.save(donThuocChiTiet);
    }
    
    public List<DonThuocChiTiet> getByDonThuocId(ObjectId donThuocId) {
        return donThuocChiTietRepository.findByDonThuocRefObjectIdAndTrangThai(donThuocId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public long countByNgayUongThuoc(ObjectId hoSoBenhAnId, Date ngayUongThuoc) {
        var query = new Query(Criteria.where("hoSoBenhAnRef.objectId").is(hoSoBenhAnId).and("ngayBatDau")
                .lt(ngayUongThuoc).and("ngayKetThuc").gt(ngayUongThuoc));
        
        return mongoTemplate.count(query, DonThuocChiTiet.class);
    }
        
	public List<DonThuocChiTiet> getByNgayUongThuoc(ObjectId hoSoBenhAnId, Date ngayUongThuoc,
			int offset, int limit) {

		var sort = new Sort(Sort.Direction.ASC, "id");

		var query = new Query(Criteria.where("hoSoBenhAnRef.objectId").is(hoSoBenhAnId).and("ngayBatDau")
				.lt(ngayUongThuoc).and("ngayKetThuc").gt(ngayUongThuoc))
				.with(sort);

		if (offset >= 0 && limit >= 0) {
			query = query.skip(offset).limit(limit);
		}

		return mongoTemplate.find(query, DonThuocChiTiet.class);

	}
	
	public void deleteByDonThuoc(@Nonnull ObjectId donThuocId) {
	    var dsDtct = donThuocChiTietRepository.findByDonThuocRefObjectIdAndTrangThai(donThuocId, TRANGTHAI_DULIEU.DEFAULT);
	    for(var dtct : dsDtct) {
	        donThuocChiTietRepository.delete(dtct);
	    }
	}
}
