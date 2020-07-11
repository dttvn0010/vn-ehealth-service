package vn.ehealth.cdr.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.DichVuKyThuat;
import vn.ehealth.cdr.model.Ylenh;
import vn.ehealth.cdr.repository.DichVuKyThuatRepository;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DVKT;

@Service
public class DichVuKyThuatService {

    @Autowired private DichVuKyThuatRepository dichVuKyThuatRepository;
    @Autowired private MongoTemplate mongoTemplate;
    
    public Optional<DichVuKyThuat> getById(ObjectId id) {
        return dichVuKyThuatRepository.findById(id);
    }
    
    public DichVuKyThuat getByIdhis(String idhis) {
        var critera =  Criteria.where("idhis").is(idhis)
                                .and("trangThai").ne(TRANGTHAI_DVKT.DA_XOA);
        var query = new Query(critera);
        
        return mongoTemplate.findOne(query, DichVuKyThuat.class);
    }
    
    public List<DichVuKyThuat> getByHsbaIdAndLoaiDVKT(ObjectId hsbaId, String maLoaiDVKT) {
        var critera =  Criteria.where("hoSoBenhAnRef.objectId").is(hsbaId)
                .and("dmLoaiDVKT.ma").is(maLoaiDVKT)
                .and("trangThai").ne(TRANGTHAI_DVKT.DA_XOA);
        var query = new Query(critera);
        
        return mongoTemplate.find(query, DichVuKyThuat.class);
    }    
    
    public List<DichVuKyThuat> getByYlenhId(ObjectId ylenhId) {
        var critera =  Criteria.where("ylenhRef.objectId").is(ylenhId)                
                            .and("trangThai").ne(TRANGTHAI_DVKT.DA_XOA);
        var query = new Query(critera);
        
        return mongoTemplate.find(query, DichVuKyThuat.class);
    }
    
    public DichVuKyThuat save(@Nonnull DichVuKyThuat dvkt) {
        return dichVuKyThuatRepository.save(dvkt);
    }
   
    public DichVuKyThuat createOrUpdate(@Nonnull Ylenh ylenh, @Nonnull DichVuKyThuat dvkt) {
        
        if(dvkt.idhis != null) {
            var dvktOld = getByIdhis(dvkt.idhis); 
            dvkt.id = dvktOld != null? dvktOld.id : null;
        }
        
        dvkt.hoSoBenhAnRef = ylenh.hoSoBenhAnRef;
        dvkt.benhNhanRef = ylenh.benhNhanRef;
        dvkt.coSoKhamBenhRef = ylenh.coSoKhamBenhRef;
        dvkt.ylenhRef = Ylenh.toEmrRef(ylenh);
        return dichVuKyThuatRepository.save(dvkt);
               
    }
    
    public void deleteByYlenhId(ObjectId ylenhId) {
        var dsDvkt = getByYlenhId(ylenhId);
        for(var dvkt: dsDvkt) {
            dvkt.trangThai = TRANGTHAI_DVKT.DA_XOA;
            dichVuKyThuatRepository.save(dvkt);
        }
    }
}
