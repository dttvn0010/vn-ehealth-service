package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.BenhNhan;
import vn.ehealth.cdr.repository.BenhNhanRepository;
import vn.ehealth.cdr.utils.JsonUtil;
import vn.ehealth.cdr.utils.CDRConstants.MA_HANH_DONG;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DULIEU;

@Service
public class BenhNhanService {

    @Autowired 
    private MongoTemplate mongoTemplate;
    
    @Autowired 
    private BenhNhanRepository benhNhanRepository;
    
    @Autowired 
    private LogService logService;
    
    public BenhNhan createOrUpdate(BenhNhan benhNhan, String jsonSt) {
        var benhNhanOld = getByIdDinhDanhChinh(benhNhan.idDinhDanhChinh);
        benhNhan.id = benhNhanOld != null? benhNhanOld.id : null;
        
        benhNhan = benhNhanRepository.save(benhNhan);
        
        logService.logAction(BenhNhan.class.getName(), benhNhan.id, MA_HANH_DONG.THEM_SUA, new Date(), null, 
                                    JsonUtil.dumpObject(benhNhan), jsonSt);
        
        return benhNhan;
    }
    
    public BenhNhan getById(ObjectId id) {
        var benhNhan = benhNhanRepository.findById(id);
        if(benhNhan.isPresent() && benhNhan.get().trangThai != TRANGTHAI_DULIEU.DA_XOA) {
            return benhNhan.get();
        }
        return null;
    }
    
    public BenhNhan getByIdDinhDanhChinh(String idDinhDanhChinh) {
        var criteria =  Criteria.where("idDinhDanhChinh").is(idDinhDanhChinh)
                                 .and("trangThai").ne(TRANGTHAI_DULIEU.DA_XOA);
    	return mongoTemplate.findOne(new Query(criteria), BenhNhan.class);
    }
    
    public BenhNhan getByIdhis(String idhis) {
        var criteria =  Criteria.where("idhis").is(idhis)
                                 .and("trangThai").ne(TRANGTHAI_DULIEU.DA_XOA);
        return mongoTemplate.findOne(new Query(criteria), BenhNhan.class);
    }
    
    public BenhNhan getBySobhyt(String sobhyt) {
        var criteria =  Criteria.where("sobhyt").is(sobhyt)
                .and("trangThai").ne(TRANGTHAI_DULIEU.DA_XOA);
        
        return mongoTemplate.findOne(new Query(criteria), BenhNhan.class);
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
