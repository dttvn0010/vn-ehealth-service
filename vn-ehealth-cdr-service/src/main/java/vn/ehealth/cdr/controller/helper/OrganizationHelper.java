package vn.ehealth.cdr.controller.helper;

import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import vn.ehealth.hl7.fhir.provider.dao.impl.OrganizationDao;
import vn.ehealth.utils.MongoUtils;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

import org.hl7.fhir.r4.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizationHelper {
    
    @Autowired private OrganizationDao organizationDao; 
        
    public Organization getOrganizationByMa(String maCskb) {

        var params = mapOf(
                "identifier.system", (Object) IdentifierSystem.PROVIDER,
                "identifier.value", maCskb
            );

        var query = MongoUtils.createQuery(params);
        return organizationDao.getResource(query);
    }
}
