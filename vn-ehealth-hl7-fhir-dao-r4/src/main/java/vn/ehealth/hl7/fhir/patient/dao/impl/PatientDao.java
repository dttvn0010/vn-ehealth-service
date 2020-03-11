package vn.ehealth.hl7.fhir.patient.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.patient.dao.IPatient;
import vn.ehealth.hl7.fhir.patient.dao.transform.PatientEntityToFHIRPatient;
import vn.ehealth.hl7.fhir.patient.entity.PatientEntity;

@Repository
public class PatientDao implements IPatient {

    @Autowired
    MongoOperations mongo;

    @Autowired
    PatientEntityToFHIRPatient patientEntityToFHIRPatient;

    @Override
    public Patient create(FhirContext fhirContext, Patient object) {
        PatientEntity entity = null;
        int version = ConstantKeys.VERSION_1;
        if (object != null) {
            entity = createNewPatientEntity(object, version, null);
            // save PatientEntity database
            mongo.save(entity);
            Date cal = new Date();
            Patient patient = patientEntityToFHIRPatient.transform(entity);
            Date cal1 = new Date();
            System.out.println("-------------------create tranform end------------------"+(cal1.getTime()-cal.getTime())+" ms");
            return patient;
        }
        return null;
    }

    @Override
    @CachePut(value = "patient", key = "#idType")
    public Patient update(FhirContext fhirContext, Patient object, IdType idType) {
        PatientEntity entityOld = null;
        String fhirId = "";
        if (idType != null && idType.hasIdPart()) {
            fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true)).withHint("idx_by_fhir_id-active");
            entityOld = mongo.findOne(query, PatientEntity.class);
        }
        if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {
            // remove PatientEntity old
            entityOld.resDeleted = (new Date());
            entityOld.active = (false);
            mongo.save(entityOld);
            // save PatientEntity
            int version = entityOld.version + 1;
            if (object != null) {
                PatientEntity entity = createNewPatientEntity(object, version, fhirId);
                entity.resUpdated = (new Date());
                mongo.save(entity);
                Date cal = new Date();
                Patient patient = patientEntityToFHIRPatient.transform(entity);
                Date cal1 = new Date();
                System.out.println("-------------------update tranform end------------------"+(cal1.getTime()-cal.getTime())+" ms");
                return patient;
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = "patient", key = "#idType")
    public Patient read(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true)).withHint("idx_by_fhir_id-active");
            PatientEntity entity = mongo.findOne(query, PatientEntity.class);
            if (entity != null) {
                Date cal = new Date();
                Patient patient = patientEntityToFHIRPatient.transform(entity);
                Date cal1 = new Date();
                System.out.println("-------------------read tranform end------------------"+(cal1.getTime()-cal.getTime())+" ms");
                return patient;
            }
        }
        return null;
    }

    @Override
    @CacheEvict(value = "patient", key = "#idType")
    public Patient remove(FhirContext fhirContext, IdType idType) {
        if (idType != null && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Query query = Query
                    .query(Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_ACTIVE).is(true)).withHint("idx_by_fhir_id-active");
            PatientEntity entity = mongo.findOne(query, PatientEntity.class);
            if (entity != null) {
                entity.active = (false);
                entity.resDeleted = (new Date());
                mongo.save(entity);
                Date cal = new Date();
                Patient patient = patientEntityToFHIRPatient.transform(entity);
                Date cal1 = new Date();
                System.out.println("-------------------remove tranform end------------------"+(cal1.getTime()-cal.getTime())+" ms");
                return patient;
                
            }
        }
        return null;
    }

    @Override
    public Patient readOrVread(FhirContext fhirContext, IdType idType) {
        if (idType.hasVersionIdPart() && idType.hasIdPart()) {
            String fhirId = idType.getIdPart();
            Integer version = Integer.valueOf(idType.getVersionIdPart());
            if (version != null) {
                Query query = Query.query(
                        Criteria.where(ConstantKeys.SP_FHIR_ID).is(fhirId).and(ConstantKeys.SP_VERSION).is(version)).withHint("idx_by_fhir_id-version");
                PatientEntity entity = mongo.findOne(query, PatientEntity.class);
                if (entity != null) {
                    Date cal = new Date();
                    Patient patient = patientEntityToFHIRPatient.transform(entity);
                    Date cal1 = new Date();
                    System.out.println("-------------------readvread tranform end------------------"+(cal1.getTime()-cal.getTime())+" ms");
                    return patient;
                }
            }
        }
        return null;
    }

    @Override
    public List<IBaseResource> search(FhirContext ctx, TokenParam active, TokenParam addressUse, TokenParam animalBreed,
            TokenParam animalSpecies, TokenParam deceased, TokenParam email, TokenParam gender, TokenParam identifier,
            TokenParam language, TokenParam phone, TokenParam telecom, ReferenceParam generalPractitioner,
            ReferenceParam link, ReferenceParam organization, DateRangeParam birthDate, DateRangeParam deathDate,
            StringParam address, StringParam addressCity, StringParam addressCountry, StringParam addressState,
            StringParam familyName, StringParam givenName, StringParam name, StringParam phonetic, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content, StringParam _page, String sortParam, Integer count) {

        List<IBaseResource> resources = new ArrayList<IBaseResource>();

        Criteria criteria = setParamToCriteria(active, addressUse, animalBreed, animalSpecies, deceased, email, gender,
                identifier, language, phone, telecom, generalPractitioner, link, organization, birthDate, deathDate,
                address, addressCity, addressCountry, addressState, familyName, givenName, name, phonetic, resid,
                _lastUpdated, _tag, _profile, _query, _security, _content);
        // custom
        if (criteria != null) {
            Query qry = Query.query(criteria);
            Pageable pageableRequest;
            pageableRequest = new PageRequest(_page != null ? Integer.valueOf(_page.getValue()) : ConstantKeys.PAGE,
                    count != null ? count : ConstantKeys.DEFAULT_PAGE_MAX_SIZE);
            qry.with(pageableRequest);
            if (!sortParam.equals("")) {
                qry.with(new Sort(Sort.Direction.ASC, sortParam));
            }
            List<PatientEntity> patientResults = mongo.find(qry, PatientEntity.class);
            Date cal = new Date();
            for (PatientEntity patientEntity : patientResults) {
                resources.add(patientEntityToFHIRPatient.transform(patientEntity));
            }
            Date cal1 = new Date();
            System.out.println("-------------------search tranform end------------------"+(cal1.getTime()-cal.getTime())+" ms");
        }
        return resources;
    }

//    @Override
//    public List<Resource> searchAll(FhirContext ctx) {
//        List<Resource> resources = new ArrayList<>();
//        Query qry = Query.query(null);
//        List<PatientEntity> patientResults = mongo.find(qry, PatientEntity.class);
//        for (PatientEntity patientEntity : patientResults) {
//            resources.add(patientEntityToFHIRPatient.transform(patientEntity));
//        }
//        return resources;
//    }

    @Override
    public long findMatchesAdvancedTotal(FhirContext ctx, TokenParam active, TokenParam addressUse,
            TokenParam animalBreed, TokenParam animalSpecies, TokenParam deceased, TokenParam email, TokenParam gender,
            TokenParam identifier, TokenParam language, TokenParam phone, TokenParam telecom,
            ReferenceParam generalPractitioner, ReferenceParam link, ReferenceParam organization,
            DateRangeParam birthDate, DateRangeParam deathDate, StringParam address, StringParam addressCity,
            StringParam addressCountry, StringParam addressState, StringParam familyName, StringParam givenName,
            StringParam name, StringParam phonetic, TokenParam resid, DateRangeParam _lastUpdated, TokenParam _tag,
            UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {

        long count = 0;
        Criteria criteria = setParamToCriteria(active, addressUse, animalBreed, animalSpecies, deceased, email, gender,
                identifier, language, phone, telecom, generalPractitioner, link, organization, birthDate, deathDate,
                address, addressCity, addressCountry, addressState, familyName, givenName, name, phonetic, resid,
                _lastUpdated, _tag, _profile, _query, _security, _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        
        count = mongo.count(query,  PatientEntity.class);
        return count;
    }

    private Criteria setParamToCriteria(TokenParam active, TokenParam addressUse, TokenParam animalBreed,
            TokenParam animalSpecies, TokenParam deceased, TokenParam email, TokenParam gender, TokenParam identifier,
            TokenParam language, TokenParam phone, TokenParam telecom, ReferenceParam generalPractitioner,
            ReferenceParam link, ReferenceParam organization, DateRangeParam birthDate, DateRangeParam deathDate,
            StringParam address, StringParam addressCity, StringParam addressCountry, StringParam addressState,
            StringParam familyName, StringParam givenName, StringParam name, StringParam phonetic, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
        Criteria criteria = null;
        // active
        if (active != null) {
            criteria = Criteria.where("active").is(active.getValue());
        } else {
            criteria = Criteria.where("active").is(true);
        }
//                addressUse,addressUse
        if (addressUse != null) {
            criteria.and("address.addressUse").regex(addressUse.getValue());
        }
//                animalBreed,
        if (animalBreed != null) {
            // criteria.and("addresses.addressUse").regex(addressUse.getValue());
        }
//                animalSpecies,
        if (animalSpecies != null) {
            // criteria.and("addresses.addressUse").regex(addressUse.getValue());
        }
//                deceased,
        if (deceased != null) {
            // criteria.and("addresses.addressUse").regex(addressUse.getValue());
        }
//                email,
        if (email != null) {
            criteria.and("telecom.system").is("EMAIL").and("telecom.value").is(email.getValue());
        }
//                gender,
        if (gender != null) {
            criteria.and("gender").is(gender.getValue().toLowerCase());
        }
//                language,
        if (language != null) {
            criteria.and("communication.language.value").is(language.getValue());
        }
//                phone,
        if (phone != null) {
            criteria.and("telecom.system").is("PHONE").and("telecom.value").is(phone.getValue());
        }
//                telecom,
        if (telecom != null) {
            criteria.and("telecom.value").is(telecom.getValue());
        }
//                generalPractitioner,
        if (generalPractitioner != null) {
            criteria.orOperator(Criteria.where("generalPractitioner.reference").regex(generalPractitioner.getValue()),
                    Criteria.where("generalPractitioner.display").regex(generalPractitioner.getValue()),
                    Criteria.where("generalPractitioner.identifier.value").regex(generalPractitioner.getValue()),
                    Criteria.where("generalPractitioner.identifier.system").regex(generalPractitioner.getValue()));
        }
//                link,
        if (link != null) {
            criteria.orOperator(Criteria.where("link.other.reference").regex(organization.getValue()),
                    Criteria.where("link.other.display").regex(organization.getValue()),
                    Criteria.where("link.other.identifier.value").regex(organization.getValue()),
                    Criteria.where("link.other.identifier.system").regex(organization.getValue()));
        }
//                organization,
        if (organization != null) {
            criteria.orOperator(Criteria.where("managingOrganization.reference").regex(organization.getValue()),
                    Criteria.where("managingOrganization.display").regex(organization.getValue()),
                    Criteria.where("managingOrganization.identifier.value").regex(organization.getValue()),
                    Criteria.where("managingOrganization.identifier.system").regex(organization.getValue()));
        }
//                birthDate,
        if (birthDate != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "birthDate", birthDate);
        }
//                deathDate,
        if (deathDate != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "deceased", deathDate);
        }
//                address,
        if (address != null) {
            criteria.orOperator(Criteria.where("addresses.addressLine1.myStringValue").regex(address.getValue()),
                    Criteria.where("addresses.addressLine2.myStringValue").regex(address.getValue()));
        }
//                addressCity,
        if (addressCity != null) {
            criteria.and("address.city").regex(addressCity.getValue());
        }
//                addressCountry,
        if (addressCountry != null) {
            criteria.and("address.country").regex(addressCountry.getValue());
        }
//                addressState,    
        if (addressState != null) {
            criteria.and("address.state").regex(addressState.getValue());
        }
//                familyName,
        if (familyName != null) {
            if (criteria == null) {
                criteria = Criteria.where("name.family").regex(familyName.getValue());
            } else {
                criteria.and("name.family").regex(familyName.getValue());
            }
        }
//                givenName ,   
        if (givenName != null) {
            if (criteria == null) {
                criteria = Criteria.where("name.given").regex(givenName.getValue());
            } else {
                criteria.and("name.given").regex(givenName.getValue());
            }
        }
//                name,
        if (name != null) {
            String regexName = name.getValue(); // .toLowerCase()+".*"; // use options = i for regex
            criteria.orOperator(Criteria.where("name.family").regex(regexName),
                    Criteria.where("name.given").regex(regexName));
        }
//                phonetic,
        if (phonetic != null) {
            String regexName = phonetic.getValue(); // .toLowerCase()+".*"; // use options = i for regex
            criteria.orOperator(Criteria.where("name.family").regex(regexName),
                    Criteria.where("name.given").regex(regexName));
        }
        // default
        criteria = DatabaseUtil.addParamDefault2Criteria(criteria, resid, _lastUpdated, _tag, _profile, _security,
                identifier);
        return criteria;
    }

    private PatientEntity createNewPatientEntity(Patient obj, int version, String fhirId) {
        var ent = PatientEntity.fromPatient(obj);
        DataConvertUtil.setMetaExt(obj, ent);
        if (fhirId != null && !fhirId.isEmpty()) {
            ent.fhir_id = (fhirId);
        } else {
            ent.fhir_id = (StringUtil.generateUID());
        }
        
        ent.active = (true);
        ent.version = (version);
        ent.resCreated = (new Date());
        return ent;
    }

    public List<Patient> getPatientHistory(IdType theId,InstantType theSince, DateRangeParam theAt){
        List<Patient> retVal = new ArrayList<Patient>();
        Criteria criteria = null;
        criteria = Criteria.where("$where").is("1==1");
        if (theId != null && theId.hasIdPart()) {
            String fhirId = theId.getIdPart();
            criteria.and(ConstantKeys.SP_FHIR_ID).is(fhirId);
        }
        if (theAt != null) {
            criteria = DatabaseUtil.setTypeDateToCriteria(criteria, "resCreated", theAt);
        }
        if (theSince != null) {
            Date dateNow = new Date();
            Date dateParam = theSince.getValue();
            criteria.and("resCreated").gte(dateParam).lte(dateNow);
        }
        if (criteria != null) {
            Query qry = Query.query(criteria).withHint("idx_by_fhir_id-version");
            List<PatientEntity> patientResults = mongo.find(qry, PatientEntity.class);
            Date cal = new Date();
            for (PatientEntity patientEntity : patientResults) {
                retVal.add(patientEntityToFHIRPatient.transform(patientEntity));
            }
            Date cal1 = new Date();
            System.out.println("-------------------history tranform end------------------"+(cal1.getTime()-cal.getTime())+" ms");
        }
        return retVal;
    }

}
