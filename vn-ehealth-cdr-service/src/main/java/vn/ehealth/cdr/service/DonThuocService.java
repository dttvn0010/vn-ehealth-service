package vn.ehealth.cdr.service;

import java.util.List;
import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.DonThuoc;
import vn.ehealth.cdr.model.Ylenh;
import vn.ehealth.cdr.model.component.EmrRef;
import vn.ehealth.cdr.repository.DonThuocRepository;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DONTHUOC;
import vn.ehealth.hl7.fhir.core.util.FPUtil;

@Service
public class DonThuocService {

    @Autowired private DonThuocRepository donThuocRepository;
    @Autowired private DonThuocChiTietService donThuocChiTietService;
    @Autowired private YlenhService ylenhService;
    @Autowired private MongoTemplate mongoTemplate;
    
    public DonThuoc getById(ObjectId id) {
        var donThuoc = donThuocRepository.findById(id);
        if(donThuoc.isPresent() && donThuoc.get().trangThai != TRANGTHAI_DONTHUOC.DA_XOA) {
            return donThuoc.get();
        }
        return null;
    }
    
    public List<DonThuoc> getByHoSoBenhAnId(ObjectId hoSoBenhAnId) {
        var criteria = Criteria.where("hoSoBenhAnRef.objectId").is(hoSoBenhAnId)
                                .and("trangThai").ne(TRANGTHAI_DONTHUOC.DA_XOA);
        
        return mongoTemplate.find(new Query(criteria), DonThuoc.class);
    }
    
    public List<DonThuoc> getByYlenhId(ObjectId ylenhId) {
        var criteria = Criteria.where("ylenhRef.objectId").is(ylenhId)
                .and("trangThai").ne(TRANGTHAI_DONTHUOC.DA_XOA);

        return mongoTemplate.find(new Query(criteria), DonThuoc.class);
    }
    
    public DonThuoc getByIdhis(String idhis) {
        var criteria = Criteria.where("idhis").is(idhis)
                .and("trangThai").ne(TRANGTHAI_DONTHUOC.DA_XOA);

        return mongoTemplate.findOne(new Query(criteria), DonThuoc.class);
    }
    
    public DonThuoc save(@Nonnull DonThuoc donThuoc) {
        return donThuocRepository.save(donThuoc);
    }
    
    public DonThuoc createOrUpdate(@Nonnull Ylenh ylenh, @Nonnull DonThuoc donThuoc) {
        if(donThuoc.idhis != null) {
            var donThuocOld = getByIdhis(donThuoc.idhis);
            donThuoc.id = donThuocOld != null ? donThuocOld.id : null;
        }
        donThuoc.ylenhRef = Ylenh.toEmrRef(ylenh);
        donThuoc.hoSoBenhAnRef = ylenh.hoSoBenhAnRef;
        donThuoc.benhNhanRef = ylenh.benhNhanRef;
        donThuoc.coSoKhamBenhRef = ylenh.coSoKhamBenhRef;
        donThuoc.bacSiKeDon = ylenh.bacSiRaYlenh;
        donThuoc.ngayKeDon = ylenh.ngayRaYlenh;
        donThuoc =  donThuocRepository.save(donThuoc);
        
        if(donThuoc.dsDonThuocChiTiet != null) {
            for(var dtct : donThuoc.dsDonThuocChiTiet) {
                dtct.soDon = donThuoc.soDon;
                dtct.ylenhRef = donThuoc.ylenhRef;
                dtct.hoSoBenhAnRef = donThuoc.hoSoBenhAnRef;
                dtct.benhNhanRef = donThuoc.benhNhanRef;
                dtct.coSoKhamBenhRef = donThuoc.coSoKhamBenhRef;
                dtct.donThuocRef = DonThuoc.toEmrRef(donThuoc);
                dtct.bacSiKeDon = donThuoc.bacSiKeDon;
                dtct.ngayKeDon = donThuoc.ngayKeDon;
                donThuocChiTietService.save(dtct);
            }
        }
        
        return donThuoc;
    }
    
    public DonThuoc updateTrangThai(@Nonnull ObjectId id) {
        var donThuoc = getById(id);
        if(donThuoc != null) {
            var dsDtct = donThuocChiTietService.getByDonThuocId(id);
            if(FPUtil.allMatch(dsDtct, x -> x.trangThai == TRANGTHAI_DONTHUOC.DA_XONG)) {
                donThuoc.trangThai = TRANGTHAI_DONTHUOC.DA_XONG;
                donThuocRepository.save(donThuoc);
            }
            var ylenhId = EmrRef.toObjectId(donThuoc.ylenhRef);
            if(ylenhId != null) {
                ylenhService.updateTrangThai(ylenhId);
            }
        }
        
        return donThuoc;        
    }
    
    public void deleteByYlenhId(@Nonnull ObjectId ylenhId) {
        var dsDonThuoc = getByYlenhId(ylenhId);
        for(var donThuoc : dsDonThuoc) {
            donThuocChiTietService.deleteByDonThuoc(donThuoc.id);
            donThuoc.trangThai = TRANGTHAI_DONTHUOC.DA_XOA;
            donThuocRepository.save(donThuoc);
        }
    }
}
