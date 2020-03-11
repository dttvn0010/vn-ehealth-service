package vn.ehealth.emr.cda;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.openhealthtools.mdht.uml.cda.Act;
import org.openhealthtools.mdht.uml.cda.AssignedEntity;
import org.openhealthtools.mdht.uml.cda.CDAFactory;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.openhealthtools.mdht.uml.cda.EncompassingEncounter;
import org.openhealthtools.mdht.uml.cda.Observation;
import org.openhealthtools.mdht.uml.cda.Patient;
import org.openhealthtools.mdht.uml.cda.PatientRole;
import org.openhealthtools.mdht.uml.cda.Procedure;
import org.openhealthtools.mdht.uml.cda.Section;
import org.openhealthtools.mdht.uml.cda.StrucDocText;
import org.openhealthtools.mdht.uml.hl7.datatypes.CD;
import org.openhealthtools.mdht.uml.hl7.datatypes.CE;
import org.openhealthtools.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.openhealthtools.mdht.uml.hl7.datatypes.ED;
import org.openhealthtools.mdht.uml.hl7.datatypes.II;
import org.openhealthtools.mdht.uml.hl7.datatypes.IVL_TS;
import org.openhealthtools.mdht.uml.hl7.datatypes.ON;
import org.openhealthtools.mdht.uml.hl7.datatypes.PN;
import org.openhealthtools.mdht.uml.hl7.datatypes.ST;
import org.openhealthtools.mdht.uml.hl7.datatypes.TS;
import org.openhealthtools.mdht.uml.hl7.vocab.ActClassObservation;
import org.openhealthtools.mdht.uml.hl7.vocab.ParticipationType;
import org.openhealthtools.mdht.uml.hl7.vocab.x_ActClassDocumentEntryAct;
import org.openhealthtools.mdht.uml.hl7.vocab.x_ActMoodDocumentObservation;
import org.openhealthtools.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship;
import org.openhealthtools.mdht.uml.hl7.vocab.x_ActRelationshipExternalReference;
import org.openhealthtools.mdht.uml.hl7.vocab.x_DocumentActMood;
import org.openhealthtools.mdht.uml.hl7.vocab.x_DocumentSubstanceMood;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import test.CDATemplateFactory;
import vn.ehealth.emr.model.EmrBenhAn;
import vn.ehealth.emr.model.EmrBenhNhan;
import vn.ehealth.emr.model.EmrCoSoKhamBenh;
import vn.ehealth.emr.model.EmrDmContent;
import vn.ehealth.emr.model.EmrFileDinhKem;
import vn.ehealth.emr.model.EmrHoSoBenhAn;
import vn.ehealth.emr.model.EmrPhauThuatThuThuat;
import vn.ehealth.emr.model.EmrVaoKhoa;
import vn.ehealth.emr.utils.DateUtil;

public class CDAExportUtil {

    private static Properties cdaProp = new Properties();
    static CDATemplateFactory cdaTemplateFactory;
    private static Logger logger = LoggerFactory.getLogger(CDAExportUtil.class);
    
    
    static {
        try {
            cdaProp.load(new ClassPathResource("cda.properties").getInputStream());
        } catch (IOException e) {
            logger.error("Cannot read fieldsConvert properties", e);
        }
        
        try {
            var file = new ClassPathResource("static/json/cda_template.json").getInputStream();
            cdaTemplateFactory = new CDATemplateFactory(file);
        }catch (IOException e) {
            logger.error("Cannot load cda template", e);
        }
        
    }
    
    static ST createST(String text) {
        return DatatypesFactory.eINSTANCE.createST(text);
    }
    
    static CE createCE(EmrDmContent emrDm) {
        var code = StringUtils.isEmpty(emrDm.ma)? null : emrDm.ma;        
        var codeSytem = StringUtils.isEmpty(emrDm.codeSystem)? null : emrDm.codeSystem;
        var displayName = StringUtils.isEmpty(emrDm.ten)? null : emrDm.ten;        
        return DatatypesFactory.eINSTANCE.createCE(code, codeSytem, null, displayName);
    }
    
    static CD createCD(EmrDmContent emrDm) {
        var code = StringUtils.isEmpty(emrDm.ma)? null : emrDm.ma;        
        var codeSytem = StringUtils.isEmpty(emrDm.codeSystem)? null : emrDm.codeSystem;
        var displayName = StringUtils.isEmpty(emrDm.ten)? null : emrDm.ten;        
        return DatatypesFactory.eINSTANCE.createCD(code, codeSytem, null, displayName);
    }
    
    static PN createPN(String text) {
        var pn = DatatypesFactory.eINSTANCE.createPN();
        pn.addText(text);
        return pn;
    }
    
    static ON createON(String text) {
        var on = DatatypesFactory.eINSTANCE.createON();
        on.addText(text);
        return on;
    }
    
    static ED createED(String text) {
        return DatatypesFactory.eINSTANCE.createED(text);
    }
    
    static TS createTS(Date date) {
        return DatatypesFactory.eINSTANCE.createTS(DateUtil.parseDateToString(date, DateUtil.yyyyMMddHHmmss));
    }
    
    static IVL_TS createIVL_TS(Date date) {
        return DatatypesFactory.eINSTANCE.createIVL_TS(DateUtil.parseDateToString(date, DateUtil.yyyyMMddHHmmss));
    }
    
    static II createII(String root) {
        return DatatypesFactory.eINSTANCE.createII(root);
    }
    
    static II createRandomII() {
        return DatatypesFactory.eINSTANCE.createII(UUID.randomUUID().toString());
    }
    
    static II createII(String root, String ext) {        
        return DatatypesFactory.eINSTANCE.createII(root, ext);
    }
    
    static StrucDocText  createStrucDocText(String text) {
        var structDocText = CDAFactory.eINSTANCE.createStrucDocText();
        structDocText.addText(text);
        return structDocText;
    }
    
    static Observation createObs() {
        var obs = CDAFactory.eINSTANCE.createObservation();
        obs.setClassCode(ActClassObservation.OBS);
        obs.setMoodCode(x_ActMoodDocumentObservation.EVN);
        return obs;
    }
    
    static Act createAct() {
        var act = CDAFactory.eINSTANCE.createAct();
        act.setClassCode(x_ActClassDocumentEntryAct.ACT);
        act.setMoodCode(x_DocumentActMood.EVN);
        return act;        
    }
        
    static IVL_TS createIVL_TS(Date fromDate, Date toDate) {
        var ivl_ts =  DatatypesFactory.eINSTANCE.createIVL_TS();
        
        var lowTime = DatatypesFactory.eINSTANCE.createIVXB_TS();
        ivl_ts.setLow(lowTime);
        
        if(fromDate != null) {
            lowTime.setValue(DateUtil.parseDateToString(fromDate, DateUtil.yyyyMMddHHmmss));
        }
        
        var highTime = DatatypesFactory.eINSTANCE.createIVXB_TS();
        ivl_ts.setHigh(highTime);
        
        if(toDate != null) {
            highTime.setValue(DateUtil.parseDateToString(toDate, DateUtil.yyyyMMddHHmmss));
        }
        return ivl_ts;
    }
    
    public static void addPatientInformation(Patient patient, PatientRole patientRole, EmrHoSoBenhAn hsba){
        var emrBenhNhan = hsba.getEmrBenhNhan();

        if(!StringUtils.isEmpty(emrBenhNhan.iddinhdanhchinh)){
            patientRole.getIds().add(createII(cdaProp.getProperty("OID_DINHDANHYTEQUOCGIA"), emrBenhNhan.iddinhdanhchinh));
        }
        
        if(!StringUtils.isEmpty(emrBenhNhan.iddinhdanhphu)){
            patientRole.getIds().add(createII(cdaProp.getProperty("OID_CMND_HOCHIEU"), emrBenhNhan.iddinhdanhphu));
        }
        
        if(!StringUtils.isEmpty(emrBenhNhan.sobhyt)){
            patientRole.getIds().add(createII(cdaProp.getProperty("OID_DM_SO_BHYT"), emrBenhNhan.sobhyt));
            
            if(emrBenhNhan.ngayhethanthebhyt != null){
                var po = CDAFactory.eINSTANCE.createOrganization();
                patientRole.setProviderOrganization(po);
                
                var opo = CDAFactory.eINSTANCE.createOrganizationPartOf();
                po.setAsOrganizationPartOf(opo);
                
                opo.setEffectiveTime(createIVL_TS(emrBenhNhan.ngayhethanthebhyt));                
            }
        }
        
        if(!StringUtils.isEmpty(emrBenhNhan.tendaydu)){
            patient.getNames().add(createPN(emrBenhNhan.tendaydu));   
        }
        
        if(emrBenhNhan.ngaysinh != null){
            patient.setBirthTime(createTS(emrBenhNhan.ngaysinh));
        }

        if(emrBenhNhan.emrDmGioiTinh != null){            
            patient.setAdministrativeGenderCode(createCE(emrBenhNhan.emrDmGioiTinh));
        }

        if(emrBenhNhan.emrDmDanToc != null){
            patient.setEthnicGroupCode(createCE(emrBenhNhan.emrDmDanToc));
        }
                    
        var address = DatatypesFactory.eINSTANCE.createAD();     
        if(!StringUtils.isEmpty(emrBenhNhan.diachi)){         
            address.addStreetAddressLine(emrBenhNhan.diachi);
        }   
        
        StringBuilder strAddress = new StringBuilder(); 
        
        var emrDmPhuongXa = emrBenhNhan.emrDmPhuongXa;
        if(emrDmPhuongXa != null){
            if(!StringUtils.isEmpty(emrDmPhuongXa.ma)){
                address.addPrecinct(emrDmPhuongXa.ma);
            }           
            if(!StringUtils.isEmpty(emrDmPhuongXa.ten)){                    
                strAddress.append(emrDmPhuongXa.ten);
            }
        }
        
        var emrDmQuanHuyen = emrBenhNhan.emrDmQuanHuyen;
        if(emrDmQuanHuyen != null){
            if(!StringUtils.isEmpty(emrDmQuanHuyen.ma)){
                address.addCity(emrDmQuanHuyen.ma);
            }
            if(!StringUtils.isEmpty(emrDmQuanHuyen.ten)){
                strAddress.append(", ").append(emrDmQuanHuyen.ten);
            }
        }
        var emrDmTinhThanh = emrBenhNhan.emrDmTinhThanh;
        if(emrDmTinhThanh != null){
            if(!StringUtils.isEmpty(emrDmTinhThanh.ma)){
                address.addState(emrDmTinhThanh.ma);
            }
            if(!StringUtils.isEmpty(emrDmTinhThanh.ten)){
                strAddress.append(", ").append(emrDmTinhThanh.ten);
            }
        }
        
        if(!StringUtils.isEmpty(strAddress)){
            address.addDirection(strAddress.toString());
        }
        
        patientRole.getAddrs().add(address);              
    }
    
    public static void addGuardianPerson(Patient patient, EmrBenhNhan emrBenhNhan){
        var guardian = CDAFactory.eINSTANCE.createGuardian();
        patient.getGuardians().add(guardian);
        
        var guardianPerson = CDAFactory.eINSTANCE.createPerson();
        guardian.setGuardianPerson(guardianPerson); 
                    
        if(!StringUtils.isEmpty(emrBenhNhan.tennguoibaotin)){
            guardianPerson.getNames().add(createPN(emrBenhNhan.tennguoibaotin));
        }
        
        if(!StringUtils.isEmpty(emrBenhNhan.diachinguoibaotin)){  
            var ad = DatatypesFactory.eINSTANCE.createAD();
            guardian.getAddrs().add(ad);
            ad.addText(emrBenhNhan.diachinguoibaotin);         
        }
                
        if(!StringUtils.isEmpty(emrBenhNhan.sodienthoainguoibaotin)){
            var tel = DatatypesFactory.eINSTANCE.createTEL();
            guardian.getTelecoms().add(tel);
            tel.setValue(emrBenhNhan.sodienthoainguoibaotin);       
        }       
    }
    
    public static void addPatientParents(ClinicalDocument doc, EmrHoSoBenhAn hsba) throws ReflectiveOperationException{
        EmrBenhNhan emrBenhNhan = hsba.getEmrBenhNhan();
        var fParticipant = CDAFactory.eINSTANCE.createParticipant1();
        fParticipant.setTypeCode(ParticipationType.IND);
                    
        doc.getParticipants().add(fParticipant);
        
        var fAssociateEntity = CDAFactory.eINSTANCE.createAssociatedEntity(); //(AssociatedEntity) cdaTemplateFactory.createObject("FatherAssociatedEntity"); //HSBAFactory.eINSTANCE.createHsbaFatherAssociatedEntity().init();
        fParticipant.setAssociatedEntity(fAssociateEntity);
        var father = CDAFactory.eINSTANCE.createPerson();
        fAssociateEntity.setAssociatedPerson(father);
        
        if(!StringUtils.isEmpty(emrBenhNhan.hotenbo)) {
            father.getNames().add(createPN(emrBenhNhan.hotenbo));
        }
        
        //Thông tin mẹ bệnh nhân 
        var mParticipant = CDAFactory.eINSTANCE.createParticipant1();
        mParticipant.setTypeCode(ParticipationType.IND);
        doc.getParticipants().add(mParticipant);
        
        var mAssociateEntity = CDAFactory.eINSTANCE.createAssociatedEntity();//(AssociatedEntity) cdaTemplateFactory.createObject("MotherAssociatedEntity");  //HSBAFactory.eINSTANCE.createHsbaMotherAssociatedEntity().init();
        mParticipant.setAssociatedEntity(mAssociateEntity);
        var mother = CDAFactory.eINSTANCE.createPerson();
        mAssociateEntity.setAssociatedPerson(mother);
        
        if(!StringUtils.isEmpty(emrBenhNhan.hotenme)) {
            mother.getNames().add(createPN(emrBenhNhan.hotenme));
        }  
    }
    
    public static void addDvcq_Tpkhth(ClinicalDocument doc, EmrCoSoKhamBenh emrCoSoKhamBenh, EmrHoSoBenhAn hsba) throws ReflectiveOperationException{
        var dvcqParticipant = CDAFactory.eINSTANCE.createParticipant1();           
        dvcqParticipant.setTypeCode(ParticipationType.IND);
        doc.getParticipants().add(dvcqParticipant);

        var associatedEntity = CDAFactory.eINSTANCE.createAssociatedEntity();//(AssociatedEntity) cdaTemplateFactory.createObject("DvcqVaTpkhthAssociatedEntity"); //HSBAFactory.eINSTANCE.createHsbaDvcqVaTpkhthAssociatedEntity().init();
        dvcqParticipant.setAssociatedEntity(associatedEntity);
        var eAssignedPerson = CDAFactory.eINSTANCE.createPerson();
        associatedEntity.setAssociatedPerson(eAssignedPerson);
        
        if(!StringUtils.isEmpty(hsba.getTruongphongth())) {
            eAssignedPerson.getNames().add(createPN(hsba.getTruongphongth()));
        }
        
        var org = CDAFactory.eINSTANCE.createOrganization();
        if(!StringUtils.isEmpty(hsba.getDonvichuquan())) {
            org.getNames().add(createON(hsba.getDonvichuquan()));
        }
    }
    
    
    public static void input_OutputHospital(EncompassingEncounter ecpEncounter, EmrHoSoBenhAn hsba ){
        var emrQuanLyNguoiBenh =  hsba.emrQuanLyNguoiBenh;
        var effectiveTime = createIVL_TS(emrQuanLyNguoiBenh.ngaygiovaovien, emrQuanLyNguoiBenh.ngaygioravien);
        ecpEncounter.setEffectiveTime(effectiveTime);
        
        if(emrQuanLyNguoiBenh.emrDmLoaiVaoVien != null) {
            ecpEncounter.setCode(createCE((emrQuanLyNguoiBenh.emrDmLoaiVaoVien)));
        }

        var responsibleParty = CDAFactory.eINSTANCE.createResponsibleParty();
        ecpEncounter.setResponsibleParty(responsibleParty);
        
        var assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
        responsibleParty.setAssignedEntity(assignedEntity);
        assignedEntity.getIds().add(createRandomII());
        
        var organization = CDAFactory.eINSTANCE.createOrganization();      
        assignedEntity.getRepresentedOrganizations().add(organization);
        
        var tencosoyte = DatatypesFactory.eINSTANCE.createON();      
        organization.getNames().add(tencosoyte);
        String tenCoSoYte = hsba.getTenbenhvien();
        tencosoyte.addText(tenCoSoYte);      
    }
    
    public static void addPersonCreateDocument(ClinicalDocument doc,  EmrBenhAn emrBenhAn){
        var author = CDAFactory.eINSTANCE.createAuthor();
        doc.getAuthors().add(author);
        author.setTime(createTS(emrBenhAn.ngaykybenhan));
        doc.setEffectiveTime(createTS(new Date()));

        var assignAuthor = CDAFactory.eINSTANCE.createAssignedAuthor();
        author.setAssignedAuthor(assignAuthor);     
        assignAuthor.getIds().add(createRandomII());
    
        var authAssignPerson = CDAFactory.eINSTANCE.createPerson();     
        assignAuthor.setAssignedPerson(authAssignPerson);
        
        if(!StringUtils.isEmpty(emrBenhAn.bacsylambenhan)) {
        authAssignPerson.getNames().add(createPN(emrBenhAn.bacsylambenhan));
        }    
    }
    
    public static void addDean(ClinicalDocument doc, EmrVaoKhoa emrVaoKhoa){
        var authenticator = CDAFactory.eINSTANCE.createAuthenticator();       
        doc.getAuthenticators().add(authenticator);
        
        var authenticatorSignatureCode = DatatypesFactory.eINSTANCE.createCS();
        authenticator.setSignatureCode(authenticatorSignatureCode);
        authenticatorSignatureCode.setCode(cdaProp.getProperty("CDA_SIGNATURE_CODE"));
        
        if(emrVaoKhoa.ngaygiovaokhoa != null){
            authenticator.setTime(createTS(emrVaoKhoa.ngaygiovaokhoa));
        }
        
        var authenAssignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
        authenticator.setAssignedEntity(authenAssignedEntity);
        authenAssignedEntity.getIds().add(createRandomII());
                
        var authenAssignedPerson = CDAFactory.eINSTANCE.createPerson();
        authenAssignedEntity.setAssignedPerson(authenAssignedPerson);
        
        if(!StringUtils.isEmpty(emrVaoKhoa.tentruongkhoa)){
            authenAssignedPerson.getNames().add(createPN(emrVaoKhoa.tentruongkhoa));            
        }      
    }
    
    public static void addHospitalDirector(ClinicalDocument doc, EmrHoSoBenhAn hsba){
        var legalAuthenticator = CDAFactory.eINSTANCE.createLegalAuthenticator();
        doc.setLegalAuthenticator(legalAuthenticator);
        var legalSignatureCode = DatatypesFactory.eINSTANCE.createCS();
        legalAuthenticator.setSignatureCode(legalSignatureCode);
        legalSignatureCode.setCode(cdaProp.getProperty("CDA_SIGNATURE_CODE"));
        
        TS legalAuthenticatorTime = DatatypesFactory.eINSTANCE.createTS();
        legalAuthenticator.setTime(legalAuthenticatorTime);
                
        var legalAssignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
        legalAuthenticator.setAssignedEntity(legalAssignedEntity);
        legalAssignedEntity.getIds().add(createRandomII());
                
        
        var legalAssignedPerson = CDAFactory.eINSTANCE.createPerson();
        legalAssignedEntity.setAssignedPerson(legalAssignedPerson);
        
        if(!StringUtils.isEmpty(hsba.getGiamdocbenhvien())) {
            legalAssignedPerson.getNames().add(createPN(hsba.getGiamdocbenhvien()));
        }          
    }
    
    public static void addProcedureAssignedEntity(Procedure proc, AssignedEntity assignedEntity, String assignPersonName){
        assignedEntity.getIds().add(createRandomII());
        var performer = CDAFactory.eINSTANCE.createPerformer2();
        proc.getPerformers().add(performer);                    
        performer.setAssignedEntity(assignedEntity);
        
        
        var requestPerson = CDAFactory.eINSTANCE.createPerson();
        assignedEntity.setAssignedPerson(requestPerson);
        
        if(!StringUtils.isEmpty(assignPersonName)){
            requestPerson.getNames().add(createPN(assignPersonName));
        }          
    }
    
    public static void addProcedureObservationContent(Observation obs, String obsText, EmrDmContent emrDmMaBenh){
        obs.getIds().add(createRandomII());
        if(!StringUtils.isEmpty(obsText)) {
            obs.setText(createED(obsText));
        }
        if(emrDmMaBenh != null) {
            obs.setCode(createCD(emrDmMaBenh));
        }
    }
    
    public static void addProcedureExternalDocument(Procedure procedure, String url) throws IOException{   
        if(!StringUtils.isEmpty(url)){    
            String encodedExternalDocumentUrl = URLEncoder.encode(url, "UTF-8");
            
            var reference = CDAFactory.eINSTANCE.createReference();
            reference.setTypeCode(x_ActRelationshipExternalReference.REFR);
            procedure.getReferences().add(reference);
            
            var externalDocument = CDAFactory.eINSTANCE.createExternalDocument();  //HSBAFactory.eINSTANCE.createHsbaExternalDocument().init();
            reference.setExternalDocument(externalDocument);        
            var referenceValue = DatatypesFactory.eINSTANCE.createTEL();                
                                
            referenceValue.setValue(encodedExternalDocumentUrl);
            ED externalDocumentText = DatatypesFactory.eINSTANCE.createED();
            externalDocumentText.setReference(referenceValue);
            externalDocument.setText(externalDocumentText); 
        }                
    }
    
    public static void addEmrPhauThuatThuThuat(Section proceduresSection, EmrPhauThuatThuThuat pttt,  List<EmrFileDinhKem> lstEmrQuanLyFileDinhKemPttts) throws IOException{
        if(pttt != null){
            var procedure = CDAFactory.eINSTANCE.createProcedure(); //HSBAFactory.eINSTANCE.createHsbaProceduresProcedure().init();
            proceduresSection.addProcedure(procedure);
            procedure.getIds().add(createRandomII());                
            
            if(pttt.emrDmPhauThuThuat != null) {
                procedure.setCode(createCE(pttt.emrDmPhauThuThuat));
            }
            
            if(pttt.emrDmPhauThuThuat != null){   
                procedure.setCode(createCD(pttt.emrDmPhauThuThuat));                            
            }
            
            if(pttt.ngaygiopttt != null) {
                procedure.setEffectiveTime(createIVL_TS(pttt.ngaygiopttt));
            }
            
            
            //Bác sĩ thực hiện phẫu thủ thuật
            var bacsithuchienEntity = CDAFactory.eINSTANCE.createAssignedEntity(); //HSBAFactory.eINSTANCE.createHsbaSurgeonAssignedEntity().init();   
            if(!StringUtils.isEmpty(pttt.bacsithuchien)){
                addProcedureAssignedEntity(procedure, bacsithuchienEntity, pttt.bacsithuchien);
            }

            //Bác sĩ gây mê
            var bacsigaymeEntity = CDAFactory.eINSTANCE.createAssignedEntity(); //HSBAFactory.eINSTANCE.createHsbaAnesthesiologistAssignedEntity().init();
            if(!StringUtils.isEmpty(pttt.bacsygayme)){
                addProcedureAssignedEntity(procedure, bacsigaymeEntity, pttt.bacsygayme);
            }       
            
            //Hội đồng phẫu thủ thuật
            if(pttt.emrThanhVienPttts != null){
                for (var tv : pttt.emrThanhVienPttts) {
                    if(!StringUtils.isEmpty(tv.tenbacsi)){                                
                        var consultantAssignedEntity = CDAFactory.eINSTANCE.createAssignedEntity(); //HSBAFactory.eINSTANCE.createHsbaConsultantPhysicianAssignedEntity().init();
                        if(tv.emrDmVaiTro != null) {
                            consultantAssignedEntity.setCode(createCE(tv.emrDmVaiTro));
                        }                        
                        addProcedureAssignedEntity(procedure, consultantAssignedEntity, tv.tenbacsi);
                    }
                }
            }
            
            //Chẩn đoán trước phẫu thuật    
            var preoperativeER = CDAFactory.eINSTANCE.createEntryRelationship();
            preoperativeER.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
            procedure.getEntryRelationships().add(preoperativeER);  
            
            var preoperativeDiagnosisObs = CDAFactory.eINSTANCE.createObservation(); //HSBAFactory.eINSTANCE.createHsbaPreoperativeDiagnosisObservation().init();
            preoperativeER.setObservation(preoperativeDiagnosisObs);
            
            var preoperativePrimaryER = CDAFactory.eINSTANCE.createEntryRelationship();
            preoperativePrimaryER.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
            preoperativeDiagnosisObs.getEntryRelationships().add(preoperativePrimaryER);        
            
            var preoperativePrimaryObs = CDAFactory.eINSTANCE.createObservation();//HSBAFactory.eINSTANCE.createHsbaPrimaryDiagnosisObservation().init();  
            preoperativePrimaryER.setObservation(preoperativePrimaryObs);                           
            addProcedureObservationContent(preoperativePrimaryObs, pttt.motachandoantruocpt, pttt.emrDmMaBenhChandoantruoc);
        
            //Chẩn đoán sau phẫu thuật
            var postoperativeER = CDAFactory.eINSTANCE.createEntryRelationship();
            postoperativeER.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
            procedure.getEntryRelationships().add(postoperativeER);
            
            var postoperativeDiagnosisObs =  CDAFactory.eINSTANCE.createObservation();//HSBAFactory.eINSTANCE.createHsbaPostoperativeDiagnosisObservation().init();
            postoperativeER.setObservation(postoperativeDiagnosisObs);
            
            var postoperativePrimaryER = CDAFactory.eINSTANCE.createEntryRelationship();
            postoperativePrimaryER.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
            postoperativeDiagnosisObs.getEntryRelationships().add(postoperativePrimaryER);
            
            var postoperativePrimaryObs =  CDAFactory.eINSTANCE.createObservation(); //HSBAFactory.eINSTANCE.createHsbaPrimaryDiagnosisObservation().init(); 
            postoperativePrimaryER.setObservation(postoperativePrimaryObs);     
            addProcedureObservationContent(postoperativePrimaryObs,  pttt.motachandoansaupt, pttt.emrDmMaBenhChandoansau);       
            
            //Chỉ định phẫu thuật
            var procedureIndicationsER = CDAFactory.eINSTANCE.createEntryRelationship();
            procedureIndicationsER.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
            procedure.getEntryRelationships().add(procedureIndicationsER);
            
            var procedureIndicationsAct = CDAFactory.eINSTANCE.createAct(); //HSBAFactory.eINSTANCE.createHsbaProcedureIndicationsAct().init();
            procedureIndicationsAct.getIds().add(createRandomII());
            procedureIndicationsER.setAct(procedureIndicationsAct);
            if(!StringUtils.isEmpty(pttt.chidinhptt)){
                procedureIndicationsAct.setText(createED(pttt.chidinhptt));
            }
            
            //Phương pháp vô cảm
            var anesthesiaER = CDAFactory.eINSTANCE.createEntryRelationship();
            anesthesiaER.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
            procedure.getEntryRelationships().add(anesthesiaER);
            
            var anesthesiaAct = CDAFactory.eINSTANCE.createAct(); // HSBAFactory.eINSTANCE.createHsbaAnesthesiaAct().init();
            anesthesiaAct.getIds().add(createRandomII());
            anesthesiaER.setAct(anesthesiaAct);
            if(!StringUtils.isEmpty(pttt.phuongphapvocam)){
                anesthesiaAct.setText(createED(pttt.phuongphapvocam));
            }
            
            //Lược đồ phẫu thủ thuật                
            if(!StringUtils.isEmpty(pttt.luocdoptt)){
                var procedureChartER = CDAFactory.eINSTANCE.createEntryRelationship();
                procedureChartER.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
                procedure.getEntryRelationships().add(procedureChartER);
                var procedureChartAct =  CDAFactory.eINSTANCE.createAct();//HSBAFactory.eINSTANCE.createHsbaProcedureChartAct().init();
                procedureChartER.setAct(procedureChartAct);                
                procedureChartAct.setText(createED(pttt.luocdoptt));                      
            }
                
            //Trình tự (diễn tiến) phẫu thủ thuật                      
            if(!StringUtils.isEmpty(pttt.trinhtuptt)){
                var procedureDescER = CDAFactory.eINSTANCE.createEntryRelationship();
                procedureDescER.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
                procedure.getEntryRelationships().add(procedureDescER);
                var procedureDescAct = CDAFactory.eINSTANCE.createAct();//HSBAFactory.eINSTANCE.createHsbaProcedureDescriptionAct().init();
                procedureDescAct.getIds().add(createRandomII());
                procedureDescER.setAct(procedureDescAct); 
                procedureDescAct.setText(DatatypesFactory.eINSTANCE.createED(pttt.trinhtuptt));
            }
            
            //Tài liệu đính kèm 
            if(lstEmrQuanLyFileDinhKemPttts != null){
                for (var item : lstEmrQuanLyFileDinhKemPttts) {
                    addProcedureExternalDocument(procedure, item.url);        
                }
            }
        }       
    }
    
    static Section createProcedureSection(EmrHoSoBenhAn hsba) throws IOException {
        var lstEmrPhauThuatThuThuat = hsba.getEmrPhauThuatThuThuats();          
        var proceduresSection = CDAFactory.eINSTANCE.createSection(); //HSBAFactory.eINSTANCE.createHsbaProceduresSection().init();              
        II proceduresSectionId = DatatypesFactory.eINSTANCE.createII(UUID.randomUUID().toString());
        proceduresSection.setId(proceduresSectionId);
        proceduresSection.setTitle(createST(cdaProp.getProperty("TKBA_DS_PHAUTHUATTHUTHUAT_TITLE")));

        if(lstEmrPhauThuatThuThuat != null && lstEmrPhauThuatThuThuat.size() > 0){          
            for (var ptttItem : lstEmrPhauThuatThuThuat) {
                var lstEmrQuanLyFileDinhKemPttts = ptttItem.emrFileDinhKemPttts;           
                addEmrPhauThuatThuThuat(proceduresSection, ptttItem,  lstEmrQuanLyFileDinhKemPttts);
            }   
            
            //Loại phẫu thuật/thủ thuật
            var ptSupply = CDAFactory.eINSTANCE.createSupply();
            ptSupply.setCode(DatatypesFactory.eINSTANCE.createCS("PT"));
            ptSupply.setMoodCode(x_DocumentSubstanceMood.EVN);              
            ptSupply.setText(DatatypesFactory.eINSTANCE.createED(String.valueOf(true)));
            proceduresSection.addSupply(ptSupply);
            
            var ttSupply = CDAFactory.eINSTANCE.createSupply();
            ttSupply.setCode(DatatypesFactory.eINSTANCE.createCS("TT"));             
            ttSupply.setMoodCode(x_DocumentSubstanceMood.EVN);              
            ttSupply.setText(DatatypesFactory.eINSTANCE.createED(String.valueOf(true)));
            proceduresSection.addSupply(ttSupply);
        } 
        return proceduresSection;
    }
    
    public static void addProcedures(ClinicalDocument doc, EmrHoSoBenhAn hsba) throws IOException{
        doc.addSection(createProcedureSection(hsba));
    }
    

    static void fillChanDoanObservation(@Nonnull Observation obs, String text, List<EmrDmContent> emrDmMaBenhs) {
        obs.setText(createED(text));
        for(var emrDmMaBenh : emrDmMaBenhs) {
            obs.getValues().add(createCD(emrDmMaBenh));
        }
    }
    
    static void fillChanDoanObservation(@Nonnull Observation obs, String text, EmrDmContent emrDmMaBenh) {
        fillChanDoanObservation(obs, text, List.of(emrDmMaBenh));
    }
    
    static Section createChanDoanSection(EmrHoSoBenhAn hsba) throws ReflectiveOperationException {
        if(hsba.emrChanDoan != null) {
            var section = (Section) cdaTemplateFactory.createObject("chandoan_Section");
            
            // Chan doan noi den
            var entryChandoanNoiden = CDAFactory.eINSTANCE.createEntry();
            section.getEntries().add(entryChandoanNoiden);
            
            var act = (Act) cdaTemplateFactory.createObject("chandoan_Act_noiden");
            entryChandoanNoiden.setAct(act);            
            
            var entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            act.getEntryRelationships().add(entryRelationship);
            
            var obs = (Observation) cdaTemplateFactory.createObject("chandoan_Observation_chinh");
            entryRelationship.setObservation(obs);
            fillChanDoanObservation(obs, hsba.emrChanDoan.motachandoannoiden, hsba.emrChanDoan.emrDmMaBenhChandoannoiden);
            
            // Chan doan vao vien
            var entryChandoanKkb = CDAFactory.eINSTANCE.createEntry();
            section.getEntries().add(entryChandoanKkb);
            
            act = (Act) cdaTemplateFactory.createObject("chandoan_Act_vaovien");
            entryChandoanKkb.setAct(act);
                        
            entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            act.getEntryRelationships().add(entryRelationship);
            
            obs = (Observation) cdaTemplateFactory.createObject("chandoan_Observation_chinh");
            entryRelationship.setObservation(obs);
            fillChanDoanObservation(obs, hsba.emrChanDoan.motachandoankkb, hsba.emrChanDoan.emrDmMaBenhChandoankkb);
            
            // Chan doan ra vien
            var entryChandoanRavien = CDAFactory.eINSTANCE.createEntry();
            section.getEntries().add(entryChandoanRavien);
            
            act = (Act) cdaTemplateFactory.createObject("chandoan_Act_ravien");
            entryChandoanRavien.setAct(act);
            
            // Ra vien chinh
            entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            act.getEntryRelationships().add(entryRelationship);
            
            obs = (Observation) cdaTemplateFactory.createObject("chandoan_Observation_chinh");
            entryRelationship.setObservation(obs);
            fillChanDoanObservation(obs, hsba.emrChanDoan.motachandoanravienchinh, hsba.emrChanDoan.emrDmMaBenhChandoanravienchinh);
            
            // Ra vien kem theo
            entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            act.getEntryRelationships().add(entryRelationship);
            
            obs = (Observation) cdaTemplateFactory.createObject("chandoan_Observation_kemtheo");
            entryRelationship.setObservation(obs);
            fillChanDoanObservation(obs, hsba.emrChanDoan.motachandoanravienkemtheo, hsba.emrChanDoan.emrDmMaBenhChandoanravienkemtheos);
            
            // Ra vien kem nguyen nhan
            entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            act.getEntryRelationships().add(entryRelationship);
            
            obs = (Observation) cdaTemplateFactory.createObject("chandoan_Observation_nguyennhan");
            entryRelationship.setObservation(obs);
            fillChanDoanObservation(obs, hsba.emrChanDoan.motachandoanraviennguyennhan, hsba.emrChanDoan.emrDmMaBenhChandoanraviennguyennhan);
            
            // Truoc pttt
            var entryChandoanTruocPttt = CDAFactory.eINSTANCE.createEntry();
            section.getEntries().add(entryChandoanTruocPttt);
            
            act = (Act) cdaTemplateFactory.createObject("chandoan_Act_truocpt");
            entryChandoanTruocPttt.setAct(act);
                        
            entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            act.getEntryRelationships().add(entryRelationship);
            
            obs = (Observation) cdaTemplateFactory.createObject("chandoan_Observation_chinh");
            entryRelationship.setObservation(obs);
            fillChanDoanObservation(obs, hsba.emrChanDoan.motachandoantruocpt, hsba.emrChanDoan.emrDmMaBenhChandoantruocpts);
            
            // Sau pttt
            var entryChandoanSauPttt = CDAFactory.eINSTANCE.createEntry();
            section.getEntries().add(entryChandoanSauPttt);
            
            act = (Act) cdaTemplateFactory.createObject("chandoan_Act_saupt");
            entryChandoanSauPttt.setAct(act);
                        
            entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            act.getEntryRelationships().add(entryRelationship);
            
            obs = (Observation) cdaTemplateFactory.createObject("chandoan_Observation_chinh");
            entryRelationship.setObservation(obs);
            fillChanDoanObservation(obs, hsba.emrChanDoan.motachandoansaupt, hsba.emrChanDoan.emrDmMaBenhChandoansaupts);
            
            return section;
        }
        
        return null;
    }
    
    static void addDiagnoses(ClinicalDocument doc, EmrHoSoBenhAn hsba) throws ReflectiveOperationException {
        doc.addSection(createChanDoanSection(hsba));
    }
    
    public static void generateCDABody(ClinicalDocument doc, EmrHoSoBenhAn hsba) throws Exception{
        addDiagnoses(doc, hsba);
        addProcedures(doc, hsba);        
    }
    
    public static void generateCDAHeader(ClinicalDocument doc, EmrHoSoBenhAn hsba) throws Exception{
        var emrDmLoaiBenhAn = hsba.emrDmLoaiBenhAn;
        var emrQuanLyNguoiBenh = hsba.emrQuanLyNguoiBenh;
        //var emrCoSoKhamBenh = hsba.getEmrCoSoKhamBenh();
        var emrBenhNhan = hsba.getEmrBenhNhan();       
        var  emrBenhAn = hsba.getEmrBenhAn();        
        var lstEmrVaoKhoa = hsba.emrVaoKhoas;
        
        var custodian = CDAFactory.eINSTANCE.createCustodian();
        var assignedCustodian = CDAFactory.eINSTANCE.createAssignedCustodian();
        custodian.setAssignedCustodian(assignedCustodian);
        var custodianOrganization = CDAFactory.eINSTANCE.createCustodianOrganization();
        assignedCustodian.setRepresentedCustodianOrganization(custodianOrganization);
        var custodianOrganizationId = DatatypesFactory.eINSTANCE.createII(UUID.randomUUID().toString()); 
        custodianOrganization.getIds().add(custodianOrganizationId);
        var custodianName = DatatypesFactory.eINSTANCE.createON();
        custodianOrganization.setName(custodianName);
        String tenBenhVien = hsba.emrCoSoKhamBenh.ten;
        
        if(!StringUtils.isEmpty(tenBenhVien)){
            custodianName.addText(tenBenhVien);
        }   
        doc.setCustodian(custodian);
        
        if(emrDmLoaiBenhAn != null){
            doc.setCode(createCE(emrDmLoaiBenhAn));
            doc.setTitle(createST(emrDmLoaiBenhAn.ten));
        }
        
        var docId = DatatypesFactory.eINSTANCE.createII(UUID.randomUUID().toString());
        doc.setId(docId);
        
        String soLuuTru = hsba.getMaluutru();
        if(!StringUtils.isEmpty(soLuuTru)){
            doc.setSetId(createII((String) cdaProp.get("HSBA"), soLuuTru));        }
        
        var recordTarget = CDAFactory.eINSTANCE.createRecordTarget();
        doc.getRecordTargets().add(recordTarget);
        
        var patientRole = CDAFactory.eINSTANCE.createPatientRole();
        recordTarget.setPatientRole(patientRole);
        
        String strIdHis = emrBenhNhan.idhis;
        if(!StringUtils.isEmpty(strIdHis)){
            var idHis = DatatypesFactory.eINSTANCE.createII(UUID.randomUUID().toString());
            idHis.setRoot(cdaProp.getProperty("OID_IDHIS"));
            idHis.setExtension(strIdHis);
            patientRole.getIds().add(idHis);
        }
        
        var patient = CDAFactory.eINSTANCE.createPatient();
        patientRole.setPatient(patient);
                
        var emrDmLoaiDoiTuongTaiChinh = emrQuanLyNguoiBenh.emrDmLoaiDoiTuongTaiChinh;
        if(emrDmLoaiDoiTuongTaiChinh != null){
            String dttcCodeCda = emrDmLoaiDoiTuongTaiChinh.maicd;
            var dttcCodeTag = DatatypesFactory.eINSTANCE.createCE();
            if(!StringUtils.isEmpty(dttcCodeCda)){
                dttcCodeTag.setCode(dttcCodeCda);
                
                String dttcDisplayName = emrDmLoaiDoiTuongTaiChinh.ten;
                if(!StringUtils.isEmpty(dttcDisplayName)){
                    dttcCodeTag.setDisplayName(dttcDisplayName);
                }
            }               
            patient.setRaceCode(dttcCodeTag);
        }
        
        if(emrBenhNhan != null){
            //I.HÀNH CHÍNH - 1 + 2 + 3 + 5 + 7 + 10. Tên bệnh nhân + Sinh ngày + Giới tính + Dân tộc + Địa chỉ + Đối tượng BHYT, Số BHYT
            addPatientInformation(patient, patientRole, hsba);
            
            //I.HÀNH CHÍNH - 11. Họ tên người báo tin + sđt người báo tin       
            addGuardianPerson(patient, emrBenhNhan);    
                        
            //Thông tin cha mẹ bệnh nhân 
            addPatientParents(doc, hsba);
            
            //Nơi làm việc          
            //addEmployer(doc, emrBenhNhan);          
        }
        
        //Đơn vị chủ quản và Trưởng phòng kế hoạch tổng hợp
        addDvcq_Tpkhth(doc, hsba.emrCoSoKhamBenh, hsba);
        
        //15. Cơ sở khám chữa bệnh
        var componentOf = CDAFactory.eINSTANCE.createComponent1();
        doc.setComponentOf(componentOf);
        
        
        //create encompassing encounter
        var ecpEncounter = CDAFactory.eINSTANCE.createEncompassingEncounter();
        componentOf.setEncompassingEncounter(ecpEncounter);
        
        //24. Mã Y tế
        String maYte = hsba.getMayte();
        if(!StringUtils.isEmpty(maYte)){
            var ecpEncounterId = DatatypesFactory.eINSTANCE.createII();
            ecpEncounterId.setExtension(maYte);
            ecpEncounterId.setRoot(cdaProp.getProperty("OID_DINHDANHYTEQUOCGIA"));
            ecpEncounter.getIds().add(ecpEncounterId);
        }           
        if(emrQuanLyNguoiBenh != null){
            //II.QUẢN LÝ NGƯỜI BỆNH - 12+18. Ngày vào viện/Ngày ra viện
            input_OutputHospital(ecpEncounter, hsba);
        }
                
        if(emrBenhAn != null){
            //16 + 17. Ngày làm bệnh án + Bác sĩ làm bệnh án
            addPersonCreateDocument(doc, emrBenhAn);
        }

        if(lstEmrVaoKhoa.size() > 0){
            var emrVaoKhoa = lstEmrVaoKhoa.get(0);
            //20+21. Ngày ký bệnh án + Bác sĩ trưởng khoa 
            addDean(doc, emrVaoKhoa);       
        }
        
        //22. Giám đốc bệnh viện 
        addHospitalDirector(doc, hsba);
    }
}
