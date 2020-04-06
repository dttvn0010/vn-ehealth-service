package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.BenhNhan;
import vn.ehealth.cdr.repository.BenhNhanRepository;
import vn.ehealth.cdr.utils.JsonUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.MA_HANH_DONG;

@Service
public class BenhNhanService {

    @Autowired 
    private MongoTemplate mongoTemplate;
    
    @Autowired 
    private BenhNhanRepository benhNhanRepository;
    
    @Autowired 
    private LogService logService;
    
    public BenhNhan createOrUpdate(BenhNhan benhNhan, String jsonSt) {
        boolean createNew = benhNhan.id == null;
        
        benhNhan.id = benhNhanRepository
                            .findByIdDinhDanhChinh(benhNhan.idDinhDanhChinh)
                            .map(x -> x.id).orElse(null);
        
        benhNhan = benhNhanRepository.save(benhNhan);
        
        if(createNew) {
            logService.logAction(BenhNhan.class.getName(), benhNhan.id, MA_HANH_DONG.TAO_MOI, new Date(), null, 
                                        "", jsonSt);
        }
        
        logService.logAction(BenhNhan.class.getName(), benhNhan.id, MA_HANH_DONG.CHINH_SUA, new Date(), null, 
                                    JsonUtil.dumpObject(benhNhan), jsonSt);
        
        return benhNhan;
    }
    
    public Optional<BenhNhan> getById(ObjectId id) {
        return benhNhanRepository.findById(id);
    }
    
    public Optional<BenhNhan> getByIdhis(String idhis) {
    	return benhNhanRepository.findByIdhis(idhis);
    }
    
    public Optional<BenhNhan> getBySobhyt(String sobhyt) {
        return benhNhanRepository.findBySobhyt(sobhyt);
    }
    
    public long countBenhNhan(String keyword) {     
        var criteria =  new Criteria().orOperator(
                Criteria.where("tenDayDu").regex(keyword, "i"),
                Criteria.where("idDinhDanhChinh").regex(keyword, "i")
             );
        
        return mongoTemplate.count(new Query(criteria), BenhNhan.class);
    }
    
    public List<BenhNhan> searchBenhNhan(String keyword, int offset, int limit) {        
        var criteria =  new Criteria().orOperator(
                Criteria.where("tenDayDu").regex(keyword, "i"),
                Criteria.where("idDinhDanhChinh").regex(keyword, "i")
             );
        var query = new Query(criteria);
        if(limit > 0 && offset > 0) {
            query = query.skip(offset).limit(limit);
        }
        return mongoTemplate.find(new Query(criteria), BenhNhan.class);
    }
}
