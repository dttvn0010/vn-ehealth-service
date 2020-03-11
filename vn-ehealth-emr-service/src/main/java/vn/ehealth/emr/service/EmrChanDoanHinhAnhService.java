package vn.ehealth.emr.service;

import java.util.HashMap;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrChanDoanHinhAnh;
import vn.ehealth.emr.model.EmrHoSoBenhAn;
import vn.ehealth.emr.repository.EmrChanDoanHinhAnhRepository;
import vn.ehealth.emr.utils.DateUtil;

@Service
public class EmrChanDoanHinhAnhService {

	@Autowired
    private MongoTemplate mongoTemplate;
	
    @Autowired EmrChanDoanHinhAnhRepository emrChanDoanHinhAnhRepository;
    
    public List<EmrChanDoanHinhAnh> getByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        return emrChanDoanHinhAnhRepository.findByEmrHoSoBenhAnId(emrHoSoBenhAnId);
    }
    
    public void deleteAllByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        for(var cdha : getByEmrHoSoBenhAnId(emrHoSoBenhAnId)) {
            emrChanDoanHinhAnhRepository.delete(cdha);
        }
    }    
    
    public EmrChanDoanHinhAnh createOrUpdate(EmrChanDoanHinhAnh emrChanDoanHinhAnh) {
        return emrChanDoanHinhAnhRepository.save(emrChanDoanHinhAnh);
    }
    
    public void delete(ObjectId id) {
        emrChanDoanHinhAnhRepository.deleteById(id);
    }
      
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public List<HashMap> getByEmrBenhNhanId(ObjectId emrBenhNhanId) {
    	var lookupOperation = LookupOperation.newLookup()
    								.from("emr_ho_so_benh_an")
    								.localField("emrHoSoBenhAnId")
    								.foreignField("_id")
    								.as("emrHoSoBenhAns");
    	var aggregation = Aggregation.newAggregation(
    							Aggregation.match(
    								Criteria.where("emrBenhNhanId")
    										.is(emrBenhNhanId)
    								),
    								lookupOperation
    						);
    
    	var lst = mongoTemplate.aggregate(aggregation, "emr_chan_doan_hinh_anh", HashMap.class).getMappedResults();
    	lst.forEach(x -> {
    		var emrHoSoBenhAns = (List<EmrHoSoBenhAn>) x.get("emrHoSoBenhAns");
    		var emrHoSoBenhAn = emrHoSoBenhAns.size() > 0? emrHoSoBenhAns.get(0): null;
    		if(emrHoSoBenhAn != null) {
    			x.put("tenCoSoKhamBenh", emrHoSoBenhAn.getEmrCoSoKhamBenh().ten);
        		x.put("soBenhAn", emrHoSoBenhAn.matraodoi);
        		x.put("ngayVaoVien", DateUtil.parseDateToString(emrHoSoBenhAn.emrQuanLyNguoiBenh.ngaygiovaovien, "dd/MM/yyyy HH:mm"));
        		x.put("ngayRaVien", DateUtil.parseDateToString(emrHoSoBenhAn.emrQuanLyNguoiBenh.ngaygioravien, "dd/MM/yyyy HH:mm"));
    		}
    		x.put("emrHoSoBenhAns", null);
    	});
    	return lst;
    }
}
