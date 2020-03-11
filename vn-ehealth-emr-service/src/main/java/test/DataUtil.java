package test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.openhealthtools.mdht.uml.cda.Act;
import org.openhealthtools.mdht.uml.cda.CDAFactory;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.openhealthtools.mdht.uml.cda.Component3;
import org.openhealthtools.mdht.uml.cda.Entry;
import org.openhealthtools.mdht.uml.cda.EntryRelationship;
import org.openhealthtools.mdht.uml.cda.InfrastructureRootTypeId;
import org.openhealthtools.mdht.uml.cda.Observation;
import org.openhealthtools.mdht.uml.cda.Section;
import org.openhealthtools.mdht.uml.hl7.datatypes.CD;
import org.openhealthtools.mdht.uml.hl7.datatypes.CE;
import org.openhealthtools.mdht.uml.hl7.datatypes.CS;
import org.openhealthtools.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.openhealthtools.mdht.uml.hl7.datatypes.ED;
import org.openhealthtools.mdht.uml.hl7.datatypes.II;
import org.openhealthtools.mdht.uml.hl7.datatypes.ON;
import org.openhealthtools.mdht.uml.hl7.datatypes.PN;
import org.openhealthtools.mdht.uml.hl7.datatypes.ST;
import org.openhealthtools.mdht.uml.hl7.datatypes.TS;
import org.openhealthtools.mdht.uml.hl7.vocab.ActClassObservation;
import org.openhealthtools.mdht.uml.hl7.vocab.x_ActClassDocumentEntryAct;
import org.openhealthtools.mdht.uml.hl7.vocab.x_ActMoodDocumentObservation;
import org.openhealthtools.mdht.uml.hl7.vocab.x_DocumentActMood;
import org.springframework.util.StringUtils;

public class DataUtil {

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public static String createUUID() {
        return UUID.randomUUID().toString();
    }
    
	public static II createII(String root) {
        return DatatypesFactory.eINSTANCE.createII(root);
    }
    
	public static II createII(String root, String extension) {
        if(StringUtils.isEmpty(extension)) {
            extension = null;
        }
        return DatatypesFactory.eINSTANCE.createII(root, extension);
    }
	
	public static II createII(II ii) {
		return createII(ii.getRoot(), ii.getExtension());
	}
	
	public static InfrastructureRootTypeId createTypeId(String root, String extension) {
		var typeId = CDAFactory.eINSTANCE.createInfrastructureRootTypeId();
		typeId.setRoot(root);
		typeId.setExtension(extension);
		return typeId;
	}
    
	public static ST createST(String text) {
        return DatatypesFactory.eINSTANCE.createST(text);
    }
    
	public static CE createCE(String code, String codeSystem) {
        return DatatypesFactory.eINSTANCE.createCE(code, codeSystem);        
    }
        
	public static CE createCE(String code, String codeSystem, String codeSystemName, String displayName) {
        if(StringUtils.isEmpty(codeSystemName))
            codeSystemName = null;
        
        if(StringUtils.isEmpty(displayName))
            displayName = null;
        
        return DatatypesFactory.eINSTANCE.createCE(code, codeSystem, codeSystemName, displayName);        
    }
	
	public static CE createCE(CE ce) {
		return createCE(ce.getCode(), ce.getCodeSystem(), ce.getCodeSystemName(), ce.getDisplayName());
	}
    
	public static CD createCD(String code, String codeSystem, String codeSystemName, String displayName) {
        if(StringUtils.isEmpty(codeSystemName))
            codeSystemName = null;
        
        if(StringUtils.isEmpty(displayName))
            displayName = null;
        
        
        return DatatypesFactory.eINSTANCE.createCD(code, codeSystem, codeSystemName, displayName);        
    }
	
	public static CD createCD(CD cd) {
		return createCD(cd.getCode(), cd.getCodeSystem(), cd.getCodeSystemName(), cd.getDisplayName());
	}
    
	public static TS createTS(Date date) {
        if(date != null) {
            return DatatypesFactory.eINSTANCE.createTS(sdf.format(date));
        }
        return null;
    }
    
	public static CS createCS(String text) {
        return DatatypesFactory.eINSTANCE.createCS(text);
    }
 
    
	public static PN createPN(String text) {
        var pn = DatatypesFactory.eINSTANCE.createPN();
        pn.addText(text);
        return pn;
    }
    
	public static ON createON(String text) {
        var on = DatatypesFactory.eINSTANCE.createON();
        on.addText(text);
        return on;
    }
    
	public static ED createED(String text) {
        var ed = DatatypesFactory.eINSTANCE.createED();
        ed.addText(text);
        return ed;        
    }
    
	public static Observation createObs() {
        var obs = CDAFactory.eINSTANCE.createObservation();
        obs.setClassCode(ActClassObservation.OBS);
        obs.setMoodCode(x_ActMoodDocumentObservation.EVN);
        return obs;
    }
	
	public static Observation createObs(String templateId) {
		var obs = createObs();
		obs.getTemplateIds().add(createII(templateId));
		return obs;
	}
    
	public static Act createAct() {
        var act = CDAFactory.eINSTANCE.createAct();
        act.setClassCode(x_ActClassDocumentEntryAct.ACT);
        act.setMoodCode(x_DocumentActMood.EVN);
        return act;        
    }
	
	public static Act createAct(String templateId) {
		var act = createAct();
		act.getTemplateIds().add(createII(templateId));
		return act;		
	}
	
	public static Entry createEntry() {
		return CDAFactory.eINSTANCE.createEntry();
	}
	
	public static Entry createEntry(Act act) {
		var entry = CDAFactory.eINSTANCE.createEntry();
		entry.setAct(act);
		return entry;
	}
	
	public static EntryRelationship createEntryRelationship() {
		return CDAFactory.eINSTANCE.createEntryRelationship();
	}
	
	public static EntryRelationship createEntryRelationship(Observation obs ) {
		var entryRelationship = createEntryRelationship();
		entryRelationship.setObservation(obs);
		return entryRelationship;
	}
	
	public static Section createSection() {
		return CDAFactory.eINSTANCE.createSection();
	}
	
	public static Section createSection(String templateId) {
		var section = createSection();
		section.getTemplateIds().add(createII(templateId));
		return section;		
	}
	
	public static Component3 createComponent3() {
		return CDAFactory.eINSTANCE.createComponent3();		
	}
	
	public static Component3 createComponent3(Section section) {
		var component = createComponent3();
		component.setSection(section);
		return component;
	}
	
	public static ClinicalDocument createDocument() {
		return CDAFactory.eINSTANCE.createClinicalDocument();
	}
	
	public static ClinicalDocument createDocument(String templateId) {
		var document = createDocument();
		document.getTemplateIds().add(createII(templateId));
		return document;
	}
	
}
