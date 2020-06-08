package vn.ehealth.emr.controller.noitru;

import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.dto.base.CodingDTO;
import vn.ehealth.emr.dto.ehr.EncounterDTO;
import vn.ehealth.emr.dto.patient.PatientDTO;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.Constants.ExtensionURL;
import vn.ehealth.hl7.fhir.core.util.DateUtil;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.hl7.fhir.patient.dao.impl.PatientDao;


@RestController
@RequestMapping("/api/noitru/chitiet_benhnhan")
public class ChiTietBenhNhanController {

	@Autowired private EncounterDao encounterDao;
	@Autowired private PatientDao patientDao;

		
	@GetMapping("/get_detail/{encounterId}")
	public ResponseEntity<?> getDetail(@PathVariable String encounterId) {
		var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
		DatabaseUtil.setReferenceResource(encounter.getSubject());
		
		var encounterDto = EncounterDTO.fromFhir(encounter);
		var patientDto = PatientDTO.fromFhir((Patient) encounterDto.subject.resource);
		encounterDto.computes.put("patient", patientDto);
		
		return ResponseEntity.ok(encounterDto);
	}
	
	static class ThongTinBenhNhanBody {
		public String hoTen;
		public String ngaySinh;
		public CodingDTO gioiTinh;
		public CodingDTO ngheNghiep;
		public CodingDTO danToc;
		public CodingDTO quocTich;
		public CodingDTO tinhThanhPho;
		public CodingDTO quanHuyen;
		public CodingDTO phuongXa;
		public String diaChiChiTiet;
		public String maBenhNhan;
		public CodingDTO doiTuongBaoHiem;
		public String soBaoHiemYTe;
		public String noiLamViec;
		public String emailCaNhan;
		public String soDienThoai;
		
		public CodingDTO getGioiTinh() {
			return gioiTinh != null? gioiTinh : new CodingDTO();
		}
		
		public CodingDTO getNgheNghiep() {
			return ngheNghiep != null? ngheNghiep : new CodingDTO();
		}
		
		public CodingDTO getDanToc() {
			return danToc != null? danToc : new CodingDTO();
		}
		
		public CodingDTO getQuocTich() {
			return quocTich != null? quocTich : new CodingDTO();
		}
		
		public CodingDTO getTinhThanhPho() {
			return tinhThanhPho != null? tinhThanhPho : new CodingDTO();
		}
		
		public CodingDTO getQuanHuyen() {
			return quanHuyen != null? quanHuyen : new CodingDTO();
		}
		
		public CodingDTO getPhuongXa() {
			return phuongXa != null? phuongXa : new CodingDTO();
		}
		
		public CodingDTO getDoiTuongBaoHiem() {
			return doiTuongBaoHiem != null? doiTuongBaoHiem : new CodingDTO();
		}
	}
	
	@PutMapping("/update/{encounterId}")
	public ResponseEntity<?> updateInfo(@PathVariable String encounterId, @RequestBody ThongTinBenhNhanBody body) {
		try {
			var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
			var patient = patientDao.read(FhirUtil.createIdType(encounter.getSubject()));
			
			
			//patient = new Patient();
			
			var name = patient.getNameFirstRep();
			name.setText(body.hoTen);
				
			patient.setGender(AdministrativeGender.fromCode(body.gioiTinh.code));
			
			
			patient.setBirthDate(DateUtil.parseStringToDate(body.ngaySinh, "yyyyMMdd"));
			
			var ext = FhirUtil.findExtensionByURL(patient.getExtension(), ExtensionURL.NGHE_NGHIEP);
			if(ext == null) {
				ext = patient.addExtension().setUrl(ExtensionURL.NGHE_NGHIEP);
			}
			
			ext.setValue(CodingDTO.toCodeableConcept(body.ngheNghiep, CodeSystemValue.NGHE_NGHIEP));
			
			ext = FhirUtil.findExtensionByURL(patient.getExtension(), ExtensionURL.DAN_TOC);
			if(ext == null) {
				ext = patient.addExtension().setUrl(ExtensionURL.DAN_TOC);
			}
			ext.setValue(CodingDTO.toCodeableConcept(body.danToc, CodeSystemValue.DAN_TOC));
			
			ext = FhirUtil.findExtensionByURL(patient.getModifierExtension(), ExtensionURL.QUOC_TICH);
			if(ext == null) {
				ext = patient.addExtension().setUrl(ExtensionURL.QUOC_TICH);
			}
			ext.setValue(CodingDTO.toCodeableConcept(body.quocTich, CodeSystemValue.QUOC_GIA));
			
			var address = patient.addAddress();
			ext = FhirUtil.findExtensionByURL(address.getExtension(), ExtensionURL.DVHC);
			if(ext == null) {
				ext = address.addExtension().setUrl(ExtensionURL.DVHC);
			}
			
			var ext2 = FhirUtil.findExtensionByURL(ext.getExtension(), "city");
			if(ext2 == null) {
				ext2 = ext.addExtension().setUrl("city");
			}
			ext2.setValue(CodingDTO.toCodeableConcept(body.tinhThanhPho, CodeSystemValue.DVHC));
			
			ext2 = FhirUtil.findExtensionByURL(ext.getExtension(), "district");
			if(ext2 == null) {
				ext2 = ext.addExtension().setUrl("district");
			}
			ext2.setValue(CodingDTO.toCodeableConcept(body.quanHuyen, CodeSystemValue.DVHC));
			
			ext2 = FhirUtil.findExtensionByURL(ext.getExtension(), "ward");
			if(ext2 == null) {
				ext2 = ext.addExtension().setUrl("ward");
			}
			ext2.setValue(CodingDTO.toCodeableConcept(body.phuongXa, CodeSystemValue.DVHC));
			
			address.setLine(List.of(new StringType(body.diaChiChiTiet)));
			address.setText(String.format("%s, %s, %s, %s", body.diaChiChiTiet, body.getPhuongXa().display, body.getQuanHuyen().display, body.getTinhThanhPho().display));
			
			var identifier = patient.addIdentifier();
			identifier.setValue(body.maBenhNhan);
			
			ext = FhirUtil.findExtensionByURL(encounter.getExtension(), ExtensionURL.DOI_TUONG_BH);
			if(ext == null) {
				ext = encounter.addExtension().setUrl(ExtensionURL.DOI_TUONG_BH);
			}
			ext.setValue(CodingDTO.toCodeableConcept(body.doiTuongBaoHiem, CodeSystemValue.DOI_TUONG_BH));
			
			identifier = patient.addIdentifier();
			identifier.setValue(body.soBaoHiemYTe);
			
			address = patient.addAddress();
			address.setText(body.noiLamViec);
			
			var phone = FhirUtil.findContactPointBySytem(patient.getTelecom(), ContactPointSystem.PHONE);
			if(phone == null) {
				phone = patient.addTelecom().setSystem(ContactPointSystem.PHONE);
			}
			phone.setValue(body.soDienThoai);
			
			var email = FhirUtil.findContactPointBySytem(patient.getTelecom(), ContactPointSystem.EMAIL);
			if(email == null) {
				email = patient.addTelecom().setSystem(ContactPointSystem.EMAIL);
			}
			email.setValue(body.emailCaNhan);
			
			
			patientDao.update(patient, patient.getIdElement());
			encounterDao.update(encounter, encounter.getIdElement());
			
			
			return ResponseEntity.ok(Map.of("success", true));
		}catch (Exception e) {
			return ResponseUtil.errorResponse(e);
		}
	}
} 
