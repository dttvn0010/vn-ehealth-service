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

import vn.ehealth.cdr.model.ChamSoc;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.repository.ChamSocRepository;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DULIEU;

@Service
public class ChamSocService {

    @Autowired private ChamSocRepository chamSocRepository;
    @Autowired private UongThuocService uongThuocService;
    @Autowired private MongoTemplate mongoTemplate;
        
    public Optional<ChamSoc> getById(ObjectId id) {
        return chamSocRepository.findById(id);
    }
    
    public List<ChamSoc> getByHoSoBenhAnId(ObjectId hsbaId) {
        return chamSocRepository.findByHoSoBenhAnRefObjectIdAndTrangThai(hsbaId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    private Criteria createCriteriaByLoaiAndNgayChamSoc(ObjectId hoSoBenhAnId, String maLoaiChamSoc, Date ngayBatDau, Date ngayKetThuc) {
        var criteria = Criteria.where("hoSoBenhAnRef.objectId").is(hoSoBenhAnId);

        if(ngayBatDau != null && ngayKetThuc == null) {
            criteria = criteria.and("ngayChamSoc").gt(ngayBatDau);
        }
        
        if(ngayBatDau == null && ngayKetThuc != null) {
            criteria = criteria.and("ngayChamSoc").lt(ngayKetThuc);
        }
        
        if(ngayBatDau != null && ngayKetThuc != null) {
            criteria.and("ngayChamSoc").gt(ngayBatDau).lt(ngayKetThuc);
        }
        
        if(!StringUtils.isEmpty(maLoaiChamSoc)) {
            criteria.and("dmLoaiChamSoc.ma").is(maLoaiChamSoc);
        }
        
        return criteria;
    }
    
    public long countByLoaiAndNgayChamSoc(ObjectId hoSoBenhAnId, String maLoaiChamSoc, Date ngayBatDau, Date ngayKetThuc) {
        var criteria = createCriteriaByLoaiAndNgayChamSoc(hoSoBenhAnId, maLoaiChamSoc, ngayBatDau, ngayKetThuc);
        return mongoTemplate.count(new Query(criteria), ChamSoc.class);
    }
    
	public List<ChamSoc> getByLoaiAndNgayChamSoc(ObjectId hoSoBenhAnId, String maLoaiChamSoc, Date ngayBatDau, Date ngayKetThuc, int start,
			int count) {

	    var criteria = createCriteriaByLoaiAndNgayChamSoc(hoSoBenhAnId, maLoaiChamSoc, ngayBatDau, ngayKetThuc);
	    var sort = new Sort(Sort.Direction.ASC, "id");

		var query = new Query(criteria).with(sort);
		
		if(start >= 0 && count >= 0) {
		    query.skip(start).limit(count);
		}

		return mongoTemplate.find(query, ChamSoc.class);
	}
	
    public ChamSoc createOrUpdate(@Nonnull HoSoBenhAn hsba, @Nonnull ChamSoc chamSoc) {
        if(chamSoc.idhis != null) {
            chamSoc.id = chamSocRepository.findByIdhis(chamSoc.idhis).map(x -> x.id).orElse(null);
            if(chamSoc.id != null) {
                uongThuocService.deleteByChamSocId(chamSoc.id);
            }
        }
        
        chamSoc.hoSoBenhAnRef = HoSoBenhAn.toEmrRef(hsba);
        chamSoc.benhNhanRef = hsba.benhNhanRef;
        chamSoc.coSoKhamBenhRef = hsba.coSoKhamBenhRef;        
        chamSoc = chamSocRepository.save(chamSoc);
        
        return chamSoc;
    }
}
