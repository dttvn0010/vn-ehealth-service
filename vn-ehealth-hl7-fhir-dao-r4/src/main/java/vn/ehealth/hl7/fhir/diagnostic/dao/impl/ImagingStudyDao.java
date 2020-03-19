package vn.ehealth.hl7.fhir.diagnostic.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.ImagingStudy;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.dao.BaseDao;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.diagnostic.entity.ImagingStudyEntity;

@Repository
public class ImagingStudyDao extends BaseDao<ImagingStudyEntity, ImagingStudy> {

   @SuppressWarnings("deprecation")
   public List<IBaseResource> search(FhirContext fhirContext, TokenParam active, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content, StringParam _page, String sortParam, Integer count, Set<Include> includes) {
        List<IBaseResource> resources = new ArrayList<>();
        Criteria criteria = setParamToCriteria(active, resid, _lastUpdated, _tag, _profile, _query, _security,
                _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        Pageable pageableRequest;
        pageableRequest = new PageRequest(_page != null ? Integer.valueOf(_page.getValue()) : ConstantKeys.PAGE,
                count != null ? count : ConstantKeys.DEFAULT_PAGE_SIZE);
        query.with(pageableRequest);
		if (sortParam != null && !sortParam.equals("")) {
			query.with(new Sort(Sort.Direction.DESC, sortParam));
		} else {
			query.with(new Sort(Sort.Direction.DESC, "resUpdated"));
			query.with(new Sort(Sort.Direction.DESC, "resCreated"));
		}
        List<ImagingStudyEntity> ImagingStudyEntitys = mongo.find(query, ImagingStudyEntity.class);
        if (ImagingStudyEntitys != null) {
            for (ImagingStudyEntity item : ImagingStudyEntitys) {
                ImagingStudy obj = transform(item);
				// add more Resource as it's references
				if (includes != null && includes.size() > 0 && includes.contains(new Include("*"))) {
					if (obj.getSubject() != null) {
						Resource nested = DatabaseUtil.getResourceFromReference(obj.getSubject());
						if (nested != null) {
							obj.getSubject().setResource(nested);
//							if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//								resources.add(nested);
						}
					}
					if (obj.getEncounter() != null) {
						Resource nested = DatabaseUtil.getResourceFromReference(obj.getEncounter());
						if (nested != null) {
							obj.getEncounter().setResource(nested);
//							if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//								resources.add(nested);
						}
					}
					if (obj.getBasedOn() != null && obj.getBasedOn().size() > 0) {
						for (Reference ref : obj.getBasedOn()) {
							Resource nested = DatabaseUtil.getResourceFromReference(ref);
							if (nested != null) {
								ref.setResource(nested);
//								if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//									resources.add(nested);
							}
						}
					}
					if (obj.getReferrer() != null) {
						Resource nested = DatabaseUtil.getResourceFromReference(obj.getReferrer());
						if (nested != null) {
							obj.getReferrer().setResource(nested);
//							if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//								resources.add(nested);
						}
					}
					if (obj.getInterpreter() != null && obj.getInterpreter().size() > 0) {
						for (Reference ref : obj.getInterpreter()) {
							Resource nested = DatabaseUtil.getResourceFromReference(ref);
							if (nested != null) {
								ref.setResource(nested);
//								if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//									resources.add(nested);
							}
						}
					}
					if (obj.getEndpoint() != null && obj.getEndpoint().size() > 0) {
						for (Reference ref : obj.getEndpoint()) {
							Resource nested = DatabaseUtil.getResourceFromReference(ref);
							if (nested != null) {
								ref.setResource(nested);
//								if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//									resources.add(nested);
							}
						}
					}
					if (obj.getProcedureReference() != null) {
						Resource nested = DatabaseUtil.getResourceFromReference(obj.getProcedureReference());
						if (nested != null) {
							obj.getProcedureReference().setResource(nested);
//							if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//								resources.add(nested);
						}
					}
					if (obj.getLocation() != null) {
						Resource nested = DatabaseUtil.getResourceFromReference(obj.getLocation());
						if (nested != null) {
							obj.getLocation().setResource(nested);
//							if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//								resources.add(nested);
						}
					}
					if (obj.getReasonReference() != null && obj.getReasonReference().size() > 0) {
						for (Reference ref : obj.getReasonReference()) {
							Resource nested = DatabaseUtil.getResourceFromReference(ref);
							if (nested != null) {
								ref.setResource(nested);
//								if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//									resources.add(nested);
							}
						}
					}

				} else {
					if (includes != null && includes.size() > 0 && includes.contains(new Include("ImagingStudy:subject"))
							&& obj.getSubject() != null) {
						Resource nested = DatabaseUtil.getResourceFromReference(obj.getSubject());
						if (nested != null) {
							obj.getSubject().setResource(nested);
//							if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//								resources.add(nested);
						}
					}
					if (includes != null && includes.size() > 0
							&& includes.contains(new Include("ImagingStudy:encounter")) && obj.getEncounter() != null) {
						Resource nested = DatabaseUtil.getResourceFromReference(obj.getEncounter());
						if (nested != null) {
							obj.getEncounter().setResource(nested);
//							if (!FPUtil.anyMatch(resources, x -> nested.getId().equals(x.getIdElement().getValue())))
//								resources.add(nested);
						}
					}
				}
                resources.add(obj);
            }
        }
        return resources;
    }

    public long countMatchesAdvancedTotal(FhirContext fhirContext, TokenParam active, TokenParam resid,
            DateRangeParam _lastUpdated, TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security,
            StringParam _content) {
        long total = 0;
        Criteria criteria = setParamToCriteria(active, resid, _lastUpdated, _tag, _profile, _query, _security,
                _content);
        Query query = new Query();
        if (criteria != null) {
            query = Query.query(criteria);
        }
        total = mongo.count(query, ImagingStudyEntity.class);
        return total;
    }

    private Criteria setParamToCriteria(TokenParam active, TokenParam resid, DateRangeParam _lastUpdated,
            TokenParam _tag, UriParam _profile, TokenParam _query, TokenParam _security, StringParam _content) {
        Criteria criteria = null;
        // active
        if (active != null) {
            criteria = Criteria.where("active").is(active);
        } else {
            criteria = Criteria.where("active").is(true);
        }

        return criteria;
    }

    @Override
    protected String getProfile() {
        return "ImagingStudy-v1.0";
    }

    @Override
    protected ImagingStudyEntity fromFhir(ImagingStudy obj) {
        return ImagingStudyEntity.fromImagingStudy(obj);
    }

    @Override
    protected ImagingStudy toFhir(ImagingStudyEntity ent) {
        return ImagingStudyEntity.toImagingStudy(ent);
    }

    @Override
    protected Class<? extends BaseResource> getEntityClass() {
        return ImagingStudyEntity.class;
    }
}
