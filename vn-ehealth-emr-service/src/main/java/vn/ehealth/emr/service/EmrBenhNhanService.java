package vn.ehealth.emr.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrBenhNhan;
import vn.ehealth.emr.repository.EmrBenhNhanRepository;
import vn.ehealth.emr.utils.JsonUtil;
import vn.ehealth.emr.utils.Constants.MA_HANH_DONG;

@Service
public class EmrBenhNhanService {

    @Autowired 
    private MongoTemplate mongoTemplate;
    
    @Autowired 
    private EmrBenhNhanRepository emrBenhNhanRepository;
    
    @Autowired 
    private EmrLogService emrLogService;
    
    public EmrBenhNhan createOrUpdate(ObjectId userId, EmrBenhNhan emrBenhNhan, String jsonSt) {
        boolean createNew = emrBenhNhan.id == null;
        
        emrBenhNhan.id = emrBenhNhanRepository
                            .findByIddinhdanhchinh(emrBenhNhan.iddinhdanhchinh)
                            .map(x -> x.id).orElse(null);
        
        emrBenhNhan = emrBenhNhanRepository.save(emrBenhNhan);
        
        if(createNew) {
            emrLogService.logAction(EmrBenhNhan.class.getName(), emrBenhNhan.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                                        "", jsonSt);
        }
        
        emrLogService.logAction(EmrBenhNhan.class.getName(), emrBenhNhan.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                                    JsonUtil.dumpObject(emrBenhNhan), jsonSt);
        
        return emrBenhNhan;
    }
    
    public Optional<EmrBenhNhan> getById(ObjectId id) {
        return emrBenhNhanRepository.findById(id);
    }
    
    public Optional<EmrBenhNhan> getByIdhis(String idhis) {
    	return emrBenhNhanRepository.findByIdhis(idhis);
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
        var query = new Query(criteria);
        if(limit > 0 && offset > 0) {
            query = query.skip(offset).limit(limit);
        }
        return mongoTemplate.find(new Query(criteria), EmrBenhNhan.class);
    }
}
