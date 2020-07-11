package vn.ehealth.cdr.service;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.CoSoKhamBenh;
import vn.ehealth.cdr.repository.CoSoKhamBenhRepository;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DULIEU;

@Service
public class CoSoKhamBenhService {

    @Autowired private MongoTemplate mongoTemplate;
    @Autowired CoSoKhamBenhRepository coSoKhamBenhRepository;
    
    public CoSoKhamBenh getById(ObjectId id){        
        var cskb = coSoKhamBenhRepository.findById(id);
        if(cskb.isPresent() && cskb.get().trangThai != TRANGTHAI_DULIEU.DA_XOA) {
            return cskb.get();
        }
        return null;
    }
    
    public CoSoKhamBenh getByMa(String ma) {
        var criteria = Criteria.where("ma").is(ma).and("trangThai").ne(TRANGTHAI_DULIEU.DA_XOA);
        return mongoTemplate.findOne(new Query(criteria), CoSoKhamBenh.class);
    }
    
    public CoSoKhamBenh save(@Nonnull CoSoKhamBenh cskb) {
        return coSoKhamBenhRepository.save(cskb);
    }
}
