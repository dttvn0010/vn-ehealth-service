package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.DonThuocChiTiet;
import vn.ehealth.cdr.model.component.EmrRef;
import vn.ehealth.cdr.repository.DonThuocChiTietRepository;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DONTHUOC;
import vn.ehealth.hl7.fhir.core.util.FPUtil;

@Service
public class DonThuocChiTietService {

    @Autowired private DonThuocChiTietRepository donThuocChiTietRepository;
    @Autowired private DonThuocService donThuocService;
    
    @Autowired private MongoTemplate mongoTemplate;
    
    public DonThuocChiTiet getById(ObjectId id) {
        var dtct = donThuocChiTietRepository.findById(id);
        if(dtct.isPresent() && dtct.get().trangThai != TRANGTHAI_DONTHUOC.DA_XOA) {
            return dtct.get();
        }
        return null;
    }
    
    public DonThuocChiTiet save(DonThuocChiTiet donThuocChiTiet) {
        return donThuocChiTietRepository.save(donThuocChiTiet);
    }
    
    public List<DonThuocChiTiet> getByDonThuocId(ObjectId donThuocId) {
        var criteria = Criteria.where("donThuocRef.objectId").is(donThuocId)
                                .and("trangThai").ne(TRANGTHAI_DONTHUOC.DA_XOA);
        
        return mongoTemplate.find(new Query(criteria), DonThuocChiTiet.class);
    }
    
    public long countByNgayUongThuoc(ObjectId hoSoBenhAnId, Date ngayUongThuoc) {
        var query = new Query(Criteria.where("hoSoBenhAnRef.objectId").is(hoSoBenhAnId).and("ngayBatDau")
                .lt(ngayUongThuoc).and("ngayKetThuc").gt(ngayUongThuoc));
        
        return mongoTemplate.count(query, DonThuocChiTiet.class);
    }
    
    private void updateTrangThai(ObjectId hoSoBenhAnId, Date ngayUongThuoc) {
        
        var query = new Query(Criteria.where("hoSoBenhAnRef.objectId").is(hoSoBenhAnId)
                                      .and("trangThai").ne(TRANGTHAI_DONTHUOC.DA_XOA)
                                      .and("ngayKetThuc").lt(ngayUongThuoc));
        
        var dsDtct = mongoTemplate.find(query, DonThuocChiTiet.class);
        
        for(var dtct : dsDtct) {
            dtct.trangThai = TRANGTHAI_DONTHUOC.DA_XONG;
            donThuocChiTietRepository.save(dtct);
        }
        
        var donThuocIds = new HashSet<ObjectId>();
        for(var donThuocId : FPUtil.transform(dsDtct, x -> EmrRef.toObjectId(x.donThuocRef))) {
            donThuocIds.add(donThuocId);
        }
        
        for(var donThuocId : donThuocIds) {
            donThuocService.updateTrangThai(donThuocId);
        }
                            
    }
        
	public List<DonThuocChiTiet> getByNgayUongThuoc(ObjectId hoSoBenhAnId, Date ngayUongThuoc,
			int offset, int limit) {
	    
	    updateTrangThai(hoSoBenhAnId, ngayUongThuoc);
	    
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
	    var dsDtct = getByDonThuocId(donThuocId);
	    for(var dtct : dsDtct) {
	        dtct.trangThai = TRANGTHAI_DONTHUOC.DA_XOA;
	        donThuocChiTietRepository.save(dtct);
	    }
	}
}
