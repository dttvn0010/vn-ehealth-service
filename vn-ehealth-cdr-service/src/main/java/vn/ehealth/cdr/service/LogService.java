package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.Log;
import vn.ehealth.cdr.repository.LogRepository;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

@Service
public class LogService {

    Logger logger = LoggerFactory.getLogger(LogService.class);
    @Autowired LogRepository logRepository;
    @Autowired MongoTemplate mongoTemplate;
    
    public Log logAction(String objectClass, ObjectId objectId, String maHanhDong, Date ngayThucHien, ObjectId nguoiThucHienId, String noiDung, String ghiChu) {
        var log = new Log();
        log.nguoiThucHienId = nguoiThucHienId;
        log.ngayThucHien = new Date();
        log.objectId = objectId;
        log.objectClass = objectClass;
        log.maHanhDong = maHanhDong;
        log.ghiChu = ghiChu != null? ghiChu : "";
        log.noiDung = noiDung;
        return logRepository.save(log);       
    }
    
    public long countLogs(String objectClass, ObjectId objectId, String maHanhDong, boolean asc) {
        var query = new Query(Criteria.where("objectClass").is(objectClass)
                .and("objectId").is(objectId)
                .and("maHanhDong").is(maHanhDong));
        return mongoTemplate.count(query, Log.class);        
    }
    
    public List<Log> getLogs(String objectClass, ObjectId objectId, String maHanhDong, boolean asc, int offset, int limit) {
        var sortDirection = asc? Sort.Direction.ASC: Sort.Direction.DESC;
        var sort = new Sort(sortDirection, "ngayThucHien");
                    
        var query = new Query(Criteria.where("objectClass").is(objectClass)
                .and("objectId").is(objectId)
                .and("maHanhDong").is(maHanhDong))
                .with(sort);
        
        if(offset >= 0 && limit >= 0) {
            query = query.skip(offset).limit(limit);
        }
        
        return mongoTemplate.find(query, Log.class);        
    }
    
    
    public List<String> getHsbaFullLogs(ObjectId hsbaId) {
        var criteria = Criteria.where("objectClass").regex("HoSoBenhAn")
                                .and("objectId").is(hsbaId);
        
        var query = new Query(criteria);
        var lst = mongoTemplate.find(query, Log.class);
        return DataConvertUtil.transform(lst, x -> x.ghiChu);
    }
    
}
