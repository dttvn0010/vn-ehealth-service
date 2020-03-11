package test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.annotation.Nonnull;

import org.eclipse.emf.common.util.Diagnostic;
import org.openhealthtools.mdht.uml.cda.Act;
import org.openhealthtools.mdht.uml.cda.Author;
import org.openhealthtools.mdht.uml.cda.CDAFactory;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.openhealthtools.mdht.uml.cda.Component3;
import org.openhealthtools.mdht.uml.cda.Custodian;
import org.openhealthtools.mdht.uml.cda.Observation;
import org.openhealthtools.mdht.uml.cda.PatientRole;
import org.openhealthtools.mdht.uml.cda.Section;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;
import org.openhealthtools.mdht.uml.cda.util.ValidationResult;
import org.openhealthtools.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.openhealthtools.mdht.uml.hl7.vocab.ActClassObservation;
import org.openhealthtools.mdht.uml.hl7.vocab.x_ActMoodDocumentObservation;
import org.openhealthtools.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship;
import org.springframework.core.io.ClassPathResource;
import com.fasterxml.jackson.databind.ObjectMapper;

import test.TestCDA.Const.OID;
import vn.ehealth.emr.model.EmrBenhNhan;
import vn.ehealth.emr.model.EmrDmContent;
import vn.ehealth.emr.model.EmrHoSoBenhAn;
import vn.ehealth.emr.utils.EmrUtils;

public class TestCDA {
    
    static CDATemplateFactory cdaTemplateFactory;
    
    static {
        try {
            var file = new ClassPathResource("static/json/cda_template.json").getInputStream();
            cdaTemplateFactory = new CDATemplateFactory(file);
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    static class Const {
        public static final String DOC_TYPE_ID_ROOT = "2.16.840.1.113883.1.3";
        public static final String DOC_TYPE_ID_EXTENSION = "POCD_HD000040";
        
        public static final String DOC_SET_ID_EXTENSION = "180340024";
        
        
        public class OID {
            public static final String LOINC = "2.16.840.1.113883.6.1";
            public static final String SNOMED_CT = "2.16.840.1.113883.6.96";
            public static final String CONFIDENTIAL_CODE = "2.16.840.1.113883.5.25";
            public static final String HSBA = "2.16.840.1.113883.3.6000.3.1";
            public static final String LOAI_BENH_AN = "2.16.840.1.113883.3.6000.2.10";
            
            public static final String IDHIS = "2.16.840.1.113883.3.6000.1.9";        
            public static final String DINHDANHYTEQUOCGIA = "2.16.840.1.113883.3.6000.1.6";        
            public static final String CMND_HOCHIEU = "2.16.840.1.113883.3.6000.1.7";        
            public static final String DM_SO_BHYT = "2.16.840.1.113883.3.6000.1.8";
            
            public static final String DM_MA_BENH = "2.16.840.1.113883.3.6000.6.3";
        }        
        
    }
    static ObjectMapper mapper = EmrUtils.createObjectMapper();
    
    static EmrHoSoBenhAn getHsba() throws IOException {
        var file = new ClassPathResource("static/json/hsba.json").getInputStream();
        var jsonSt = new String(file.readAllBytes());
        file.close();
        
        return mapper.readValue(jsonSt, EmrHoSoBenhAn.class);
    }
    
    
    
    static ClinicalDocument createHsbaDoc(EmrHoSoBenhAn hsba) {
        var doc =  CDAFactory.eINSTANCE.createClinicalDocument();
        var typeId = CDAFactory.eINSTANCE.createInfrastructureRootTypeId();
        typeId.setRoot(Const.DOC_TYPE_ID_ROOT);
        typeId.setExtension(Const.DOC_TYPE_ID_EXTENSION);
        
        doc.setTypeId(typeId);
        doc.getTemplateIds().add(DataUtil.createII(Const.OID.HSBA));
        
        doc.setId(DataUtil.createII(DataUtil.createUUID()));
        doc.setTitle(DataUtil.createST(hsba.emrDmLoaiBenhAn.ten));
        doc.setCode(DataUtil.createCE(hsba.emrDmLoaiBenhAn.ma, Const.OID.LOAI_BENH_AN));
        doc.setEffectiveTime(DataUtil.createTS(hsba.ngayluutru));
        doc.setLanguageCode(DataUtil.createCS("vi-VN"));
        doc.setSetId(DataUtil.createII(Const.DOC_TYPE_ID_ROOT, Const.DOC_SET_ID_EXTENSION));                
        doc.setConfidentialityCode(DataUtil.createCE("N", Const.OID.CONFIDENTIAL_CODE));
        
        var component = CDAFactory.eINSTANCE.createComponent2(); 
        doc.setComponent(component);
        
        var body = CDAFactory.eINSTANCE.createStructuredBody();
        component.setStructuredBody(body);
        
        return doc;
    }
    
    static PatientRole createPatientRole(EmrBenhNhan emrBenhNhan) {
        var patientRole = CDAFactory.eINSTANCE.createPatientRole();
        patientRole.getIds().add(DataUtil.createII(Const.OID.IDHIS, emrBenhNhan.idhis));
        patientRole.getIds().add(DataUtil.createII(Const.OID.DINHDANHYTEQUOCGIA, emrBenhNhan.iddinhdanhchinh));
        patientRole.getIds().add(DataUtil.createII(Const.OID.CMND_HOCHIEU, emrBenhNhan.iddinhdanhphu));
        patientRole.getIds().add(DataUtil.createII(Const.OID.DM_SO_BHYT, emrBenhNhan.sobhyt));
        
        return patientRole;
    }
    
    static Author createAuthor(EmrHoSoBenhAn hsba) {
        var author = CDAFactory.eINSTANCE.createAuthor();
        author.setTime(DataUtil.createTS(hsba.emrBenhAn.ngaykybenhan));
        var assignedAuthor = CDAFactory.eINSTANCE.createAssignedAuthor();
        assignedAuthor.getIds().add(DataUtil.createII(DataUtil.createUUID()));
        var assignPerson = CDAFactory.eINSTANCE.createPerson();
        assignPerson.getNames().add(DataUtil.createPN(hsba.emrBenhAn.bacsylambenhan));
        assignedAuthor.setAssignedPerson(assignPerson);
        author.setAssignedAuthor(assignedAuthor);
        return author;
                
    }
    
    static Custodian createCustodian(EmrHoSoBenhAn hsba) {
        var assignedCustodian = CDAFactory.eINSTANCE.createAssignedCustodian();
        var org = CDAFactory.eINSTANCE.createCustodianOrganization();
        org.getIds().add(DataUtil.createII(DataUtil.createUUID()));
        org.setName(DataUtil.createON(hsba.emrCoSoKhamBenh.ten));
       
        assignedCustodian.setRepresentedCustodianOrganization(org);        
        
        var custodian = CDAFactory.eINSTANCE.createCustodian();
        custodian.setAssignedCustodian(assignedCustodian);
        return custodian;
    }
    
    static void fillChanDoanObservation(@Nonnull Observation obs, String text, List<EmrDmContent> emrDmMaBenhs) {
        obs.setText(DataUtil.createED(text));
        for(var emrDmMaBenh : emrDmMaBenhs) {
            obs.getValues().add(DataUtil.createCD(emrDmMaBenh.ma, OID.DM_MA_BENH, null, emrDmMaBenh.ten));
        }
    }
    
    static void fillChanDoanObservation(@Nonnull Observation obs, String text, EmrDmContent emrDmMaBenh) {
        fillChanDoanObservation(obs, text, List.of(emrDmMaBenh));
    }
    
    static Component3 createChanDoanComponent(EmrHoSoBenhAn hsba) throws ReflectiveOperationException {
        if(hsba.emrChanDoan != null) {
            var component = CDAFactory.eINSTANCE.createComponent3();
            var section = (Section) cdaTemplateFactory.createObject("chandoan_Section");
            component.setSection(section);
            
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
            
            return component;
        }
        return null;
    }
    
    public static void main(String[] args) throws Exception {
        var hsba = getHsba();
        System.out.println(hsba.matraodoi);
        
        var doc = createHsbaDoc(hsba);
        
        var recordTarget = CDAFactory.eINSTANCE.createRecordTarget();
        doc.getRecordTargets().add(recordTarget);
        recordTarget.setPatientRole(createPatientRole(hsba.emrBenhNhan));
                
        doc.getAuthors().add(createAuthor(hsba));
        doc.setCustodian(createCustodian(hsba));
        
        var components= doc.getComponent().getStructuredBody().getComponents();
        components.add(createChanDoanComponent(hsba));
        
        var tongketBenhAn = CDAFactory.eINSTANCE.createComponent3();
        components.add(tongketBenhAn);
        
        var tkbaSection = CDAFactory.eINSTANCE.createSection();
        tongketBenhAn.setSection(tkbaSection);
        
        tkbaSection.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.3.6000.4.6"));
        tkbaSection.setCode(DatatypesFactory.eINSTANCE.createCE("28655-9", "2.16.840.1.113883.6.1", "LOINC", "Physician attending Discharge summary"));
        tkbaSection.setTitle(DatatypesFactory.eINSTANCE.createST("Tổng kết bệnh án"));
        
        var text = CDAFactory.eINSTANCE.createStrucDocText();
        text.addText("TKBA");
        tkbaSection.setText(text);
        
        var dienbienLamsang = CDAFactory.eINSTANCE.createComponent5();
        tkbaSection.getComponents().add(dienbienLamsang);
        
        var dienbienLamsangSection = CDAFactory.eINSTANCE.createSection();
        dienbienLamsang.setSection(dienbienLamsangSection);
        
        dienbienLamsangSection.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.3.6000.4.6.1"));
        dienbienLamsangSection.setCode(DatatypesFactory.eINSTANCE.createCE("8648-8", "2.16.840.1.113883.6.1", "LOINC", "HOSPITAL COURSE"));
        dienbienLamsangSection.setTitle(DatatypesFactory.eINSTANCE.createST("Diễn biến lâm sàng"));
        var text2 = CDAFactory.eINSTANCE.createStrucDocText();
        text2.addText("Diễn biến lâm sàng .....");
        dienbienLamsangSection.setText(text2);
        
        var cdYHCT = CDAFactory.eINSTANCE.createComponent3();
        components.add(cdYHCT);
        
        var cdYHCTSection = CDAFactory.eINSTANCE.createSection();
        cdYHCT.setSection(cdYHCTSection);
        
        cdYHCTSection.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.3.6000.4.14"));
        cdYHCTSection.setId(DatatypesFactory.eINSTANCE.createII("2530d8cd-a169-495a-9b7a-80e66e798fcc"));
        cdYHCTSection.setCode(DatatypesFactory.eINSTANCE.createCE("10000", "2.16.840.1.113883.3.6000.6", "YHCT", "Chẩn đoán YHCT"));
        cdYHCTSection.setTitle(DatatypesFactory.eINSTANCE.createST("Chẩn đoán YHCT"));
        var text3 = CDAFactory.eINSTANCE.createStrucDocText();
        text3.addText("");
        cdYHCTSection.setText(text3);
        
        var entry = CDAFactory.eINSTANCE.createEntry();
        cdYHCTSection.getEntries().add(entry);
        
        var obs =  CDAFactory.eINSTANCE.createObservation();
        entry.setObservation(obs);
        
        obs.setClassCode(ActClassObservation.OBS);
        obs.setMoodCode(x_ActMoodDocumentObservation.EVN);
        obs.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.3.6000.5.49"));
        obs.getIds().add(DatatypesFactory.eINSTANCE.createII("76ba9305-2ab2-462d-8d27-116e3a63d8c8"));
        obs.setCode(DatatypesFactory.eINSTANCE.createCD("10100", "2.16.840.1.113883.3.6000.6", "YHCT", "Chẩn đoán vào viện"));
        
        var entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
        obs.getEntryRelationships().add(entryRelationship);
        
        entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
        var obs2 = CDAFactory.eINSTANCE.createObservation();
        entryRelationship.setObservation(obs2);
        
        obs2.setClassCode(ActClassObservation.OBS);
        obs2.setMoodCode(x_ActMoodDocumentObservation.EVN);
        obs2.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.3.6000.5.52"));
        obs2.getIds().add(DatatypesFactory.eINSTANCE.createII("0539e062-21d2-4102-8b35-9bf7b39f7c61"));
        obs2.setCode(DatatypesFactory.eINSTANCE.createCD("282291009", "2.16.840.1.113883.6.96", "SNOMED-CT", "Diagnosis"));        
        obs2.setText(DatatypesFactory.eINSTANCE.createED(""));
                
        CDAUtil.save(doc, System.out);
        
        ValidationResult result = new ValidationResult();
        CDAUtil.validate(doc, result);
        
        for (Diagnostic diagnostic : result.getErrorDiagnostics()) {
            System.out.println("ERROR: " + diagnostic.getMessage());
        }
    }
}
