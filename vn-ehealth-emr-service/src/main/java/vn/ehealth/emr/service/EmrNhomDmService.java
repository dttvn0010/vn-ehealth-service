package vn.ehealth.emr.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import vn.ehealth.emr.model.EmrNhomDm;
import vn.ehealth.emr.repository.EmrNhomDmRepository;

@Service
public class EmrNhomDmService {

    @Autowired MongoTemplate mongoTemplate;
    @Autowired EmrNhomDmRepository emrNhomDmRepository;
    
    public Optional<EmrNhomDm> getByMa(String ma){
        return emrNhomDmRepository.findByMa(ma);
    }
    
    public long countEmrNhomDm(String keyword) {
        var criteria = new Criteria().orOperator(
                Criteria.where("ten").regex(keyword),
                Criteria.where("ma").regex(keyword)
             );
        
        return mongoTemplate.count(new Query(criteria), EmrNhomDm.class);
    }
    
    public List<EmrNhomDm> getEmrNhomDmList(@RequestParam String keyword, 
                                @RequestParam Optional<Integer> offset, 
                                @RequestParam Optional<Integer> limit) {
        
        var criteria = new Criteria().orOperator(
                            Criteria.where("ten").regex(keyword),
                            Criteria.where("ma").regex(keyword)
                            );
        
        var query = new Query(criteria);
        
        if(offset.isPresent() && limit.isPresent()) {
            query = query.skip(offset.get()).limit(limit.get());
        }
        
        return mongoTemplate.find(query, EmrNhomDm.class);
    }
}
