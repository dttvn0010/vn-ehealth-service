package vn.ehealth.emr.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import vn.ehealth.emr.model.EmrDm;
import vn.ehealth.emr.model.EmrDmContent;
import vn.ehealth.emr.repository.EmrDmRepository;

@Service
public class EmrDmService {

    @Autowired MongoTemplate mongoTemplate;
    @Autowired EmrDmRepository emrDmRepository;
    @Autowired EmrNhomDmService emrNhomDmService;
    
    public EmrDmContent getEmrDmByNhom_Ma(String maNhom, String ma) {
        var emrDmContent = new EmrDmContent();
        emrDmContent.ma = emrDmContent.ten = "";
        
        var nhomId = emrNhomDmService.getByMa(maNhom).map(x -> x.id).orElse(null);
        
        if(nhomId != null) {
            var emrDm = emrDmRepository.findByEmrNhomDmIdAndMa(nhomId, ma);            
            emrDmContent.ten = emrDm.map(x -> x.ten).orElse("");
            emrDmContent.ma = emrDm.map(x -> x.ma).orElse("");
        }
        
        return emrDmContent;
    }
    
    public long countEmrDm(String maNhom, String keyword, int capdo, String maCha) {
        var nhomId = emrNhomDmService.getByMa(maNhom).map(x -> x.id).orElse(null);
        if(nhomId != null) {
            var criteria = Criteria.where("emrNhomDmId").is(nhomId);
            criteria = criteria.andOperator(
                            new Criteria().orOperator(
                                    Criteria.where("ten").regex(keyword),
                                    Criteria.where("ma").regex(keyword)
                                 )
                        );
            
            if(!StringUtils.isEmpty(maCha)) {
                var chaId = emrDmRepository.findByEmrNhomDmIdAndMa(nhomId, maCha).map(x -> x.id).orElse(null);
                criteria = criteria.and("emrDmChaId").is(chaId);
            }
            
            if(capdo > 0) {
                criteria = criteria.and("capdo").is(capdo);
            }
            return mongoTemplate.count(new Query(criteria), EmrDm.class);
        }
        return 0;
    }
    
    public List<EmrDm> getEmrDmList(String maNhom, String keyword, int capdo, 
                                        String maCha, int offset, int limit) {
        var nhomId = emrNhomDmService.getByMa(maNhom).map(x -> x.id).orElse(null);
        if(nhomId != null) {
            
            var criteria = Criteria.where("emrNhomDmId").is(nhomId);
            if(!StringUtils.isEmpty(keyword)) {
                criteria = criteria.andOperator(
                    new Criteria().orOperator(
                        Criteria.where("ten").regex(keyword),
                        Criteria.where("ma").regex(keyword)
                     )
                );
            }
           
            if(!StringUtils.isEmpty(maCha)) {
                var chaId = emrDmRepository.findByEmrNhomDmIdAndMa(nhomId, maCha).map(x -> x.id).orElse(null);
                criteria = criteria.and("emrDmChaId").is(chaId);
            }
            
            if(capdo > 0) {
                criteria = criteria.and("capdo").is(capdo);
            }
            
            var query = new Query(criteria);
            
            if(offset >= 0 && limit >= 0) {
                query = query.skip(offset).limit(limit);
            }
            
            return mongoTemplate.find(query, EmrDm.class);
        }
        return new ArrayList<>();
    }
    
    public List<EmrDm> getAllEmrDm(String maNhom) {
        var nhomId = emrNhomDmService.getByMa(maNhom).map(x -> x.id).orElse(null);
        if(nhomId != null) {            
            var criteria = Criteria.where("emrNhomDmId").is(nhomId);            
            return mongoTemplate.find(new Query(criteria), EmrDm.class);
        }
        return new ArrayList<>();
    }
    
    public void importEmrDmList(String maNhom, List<EmrDm> emrDmList) {
        var nhomId = emrNhomDmService.getByMa(maNhom).map(x -> x.id).orElse(null);
        if(nhomId != null) {
            for(var emrDm : emrDmRepository.findByEmrNhomDmId(nhomId)) {
                emrDmRepository.delete(emrDm);
            }
            
            for(var emrDm : emrDmList) {
                emrDm.emrNhomDmId = nhomId;
                emrDmRepository.save(emrDm);
            }
        }
    }
}
