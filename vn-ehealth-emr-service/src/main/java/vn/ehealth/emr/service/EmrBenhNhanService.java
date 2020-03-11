package vn.ehealth.emr.service;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrBenhNhan;
import vn.ehealth.emr.repository.EmrBenhNhanRepository;

@Service
public class EmrBenhNhanService {

    @Autowired MongoTemplate mongoTemplate;
    @Autowired EmrBenhNhanRepository emrBenhNhanRepository;
    
    public EmrBenhNhan createOrUpdate(EmrBenhNhan emrBenhNhan) {
        var emrBenhNhan0 = emrBenhNhanRepository.findByIddinhdanhchinh(emrBenhNhan.iddinhdanhchinh);
        emrBenhNhan0.ifPresent(x -> {
            emrBenhNhan.id = x.id;
        });
        return emrBenhNhanRepository.save(emrBenhNhan);
    }
    
    public Optional<EmrBenhNhan> getById(ObjectId id) {
        return emrBenhNhanRepository.findById(id);
    }
    
    public long countBenhNhan(String keyword) {     
        var criteria =  new Criteria().orOperator(
                Criteria.where("tendaydu").regex(keyword),
                Criteria.where("iddinhdanhchinh").regex(keyword)
             );
        
        return mongoTemplate.count(new Query(criteria), EmrBenhNhan.class);
    }
    
    public List<EmrBenhNhan> searchBenhNhan(String keyword, int offset, int limit) {        
        var criteria =  new Criteria().orOperator(
                Criteria.where("tendaydu").regex(keyword),
                Criteria.where("iddinhdanhchinh").regex(keyword)
             );
        if(limit >= 0 & offset >= 0) {
            var sort = new Sort(Sort.Direction.ASC, "id");
            var pageable = new OffsetBasedPageRequest(limit, offset, sort);
            return mongoTemplate.find(new Query(criteria).with(pageable), EmrBenhNhan.class);
        }else {
            return mongoTemplate.find(new Query(criteria), EmrBenhNhan.class);
        }
    }
}
