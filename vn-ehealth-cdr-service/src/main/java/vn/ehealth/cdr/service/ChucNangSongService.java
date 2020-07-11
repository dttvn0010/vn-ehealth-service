package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.ChucNangSong;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.repository.ChucNangSongRepository;
import vn.ehealth.cdr.utils.CDRConstants.MA_HANH_DONG;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DULIEU;

@Service
public class ChucNangSongService {

    @Autowired private ChucNangSongRepository chucNangSongRepository;
    @Autowired private MongoTemplate mongoTemplate;
    
    @Autowired private LogService logService;
    
    public ChucNangSong getByIdhis(String idhis) {
        var criteria = Criteria.where("idhis").is(idhis)
                .and("trangThai").ne(TRANGTHAI_DULIEU.DA_XOA);

        return mongoTemplate.findOne(new Query(criteria), ChucNangSong.class);
    }
    
    
    public List<ChucNangSong> getByHoSoBenhAnId(ObjectId hsbaId) {
        
        var criteria = Criteria.where("hoSoBenhAnRef.objectId").is(hsbaId)
                                .and("trangThai").ne(TRANGTHAI_DULIEU.DA_XOA);

        return mongoTemplate.find(new Query(criteria), ChucNangSong.class);
    }
    
    public void createOrUpdateFromHIS( @Nonnull HoSoBenhAn hsba, @Nonnull List<ChucNangSong> cnsList, @Nonnull List<Object> cnsObjList, String jsonSt) {
        for(int i = 0; i < cnsList.size(); i++) {
            var cns = cnsList.get(i);
            if(cns.idhis != null) {
                var cnsOld = getByIdhis(cns.idhis);
            	cns.id = cnsOld != null? cnsOld.id : null;
            }
            
            cns.hoSoBenhAnRef = HoSoBenhAn.toEmrRef(hsba);
            cns.benhNhanRef = hsba.benhNhanRef;
            cns.coSoKhamBenhRef = hsba.coSoKhamBenhRef;
            cns = chucNangSongRepository.save(cns);
            cnsList.set(i, cns);
        }
        
        logService.logAction(HoSoBenhAn.class.getName() + ".ChucNangSongList", hsba.id, MA_HANH_DONG.THEM_SUA, new Date(), null, 
                "", jsonSt);
    }
}
