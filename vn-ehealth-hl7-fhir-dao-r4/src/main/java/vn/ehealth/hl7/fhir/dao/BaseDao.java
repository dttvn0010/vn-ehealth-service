package vn.ehealth.hl7.fhir.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.Resource;
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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.NumberParam;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

public abstract class BaseDao<ENT extends BaseResource, FHIR extends Resource> {
	@Autowired
	protected MongoOperations mongo;

	abstract protected String getProfile();

	abstract protected Class<? extends BaseResource> getEntityClass();

	abstract protected Class<? extends Resource> getResourceClass();

	@SuppressWarnings("unchecked")
	public FHIR transform(ENT ent) {
		if (ent == null)
			return null;
		try {
			var obj = DataConvertUtil.entityToFhir(ent, getResourceClass());
			obj.setMeta(DataConvertUtil.getMeta(ent, getProfile()));
			obj.setId(ent._fhirId);
			return (FHIR) obj;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Errror================" + getEntityClass() + ",id=" + ent._fhirId);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private ENT createNewEntity(FHIR obj, int version, String fhirId) {
		ENT ent = null;
		try {
			ent = (ENT) DataConvertUtil.fhirToEntity(obj, getEntityClass());
		} catch (Exception e) {
			e.printStackTrace();
			var json = FhirContext.forR4().newJsonParser().encodeResourceToString((IBaseResource) obj);
			System.out.println("Errror===================" + json);
		}

		if (ent == null)
			return null;

		if (fhirId != null && !fhirId.isEmpty()) {
			ent._fhirId = (fhirId);
		} else {
			ent._fhirId = (StringUtil.generateUUID());
		}

		ent._active = (true);
		ent._version = (version);
		ent._resCreated = (new Date());
		ent._resUpdated = (new Date());
		ent._fhirVersion = FhirVersionEnum.R4.toString();
		return (ENT) ent;
	}

	public FHIR create(FHIR object) {
		int version = ConstantKeys.VERSION_1;
		if (object != null) {
			var entity = createNewEntity(object, version, null);
			mongo.save(entity);
			return transform(entity);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@CachePut(cacheResolver = CachingConfiguration.CACHE_RESOLVER_NAME, key = "#idType", condition = "#idType!=null")
	public FHIR update(FHIR object, IdType idType) {
		ENT entityOld = null;
		String fhirId = "";
		if (idType != null && idType.hasIdPart()) {
			fhirId = idType.getIdPart();
			Query query = Query
					.query(Criteria.where(ConstantKeys.QP_FHIRID).is(fhirId).and(ConstantKeys.QP_ACTIVE).is(true));

			entityOld = (ENT) mongo.findOne(query, getEntityClass());
		}
		if (entityOld != null && fhirId != null && !fhirId.isEmpty()) {

			// remove old entity
			entityOld._resDeleted = (new Date());
			entityOld._active = (false);
			mongo.save(entityOld);

			// save new entity
			int version = entityOld._version + 1;
			if (object != null) {
				var entity = createNewEntity(object, version, fhirId);
				entity._resUpdated = (new Date());
				mongo.save(entity);
				return transform(entity);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Cacheable(cacheResolver = CachingConfiguration.CACHE_RESOLVER_NAME, key = "#idType", condition = "#idType!=null")
	public FHIR read(IdType idType) {
		if (idType != null && idType.hasIdPart()) {
			String fhirId = idType.getIdPart();
			Query query = Query
					.query(Criteria.where(ConstantKeys.QP_FHIRID).is(fhirId).and(ConstantKeys.QP_ACTIVE).is(true));

			var entity = (ENT) mongo.findOne(query, getEntityClass());
			if (entity != null) {
				return transform(entity);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@CacheEvict(cacheResolver = CachingConfiguration.CACHE_RESOLVER_NAME, key = "#idType", condition = "#idType!=null")
	public FHIR remove(IdType idType) {
		if (idType != null && idType.hasIdPart()) {
			String fhirId = idType.getIdPart();
			Query query = Query
					.query(Criteria.where(ConstantKeys.QP_FHIRID).is(fhirId).and(ConstantKeys.QP_ACTIVE).is(true));

			var entity = (ENT) mongo.findOne(query, getEntityClass());
			if (entity != null) {
				entity._active = (false);
				entity._resDeleted = (new Date());
				mongo.save(entity);
				return transform(entity);

			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public FHIR readOrVread(IdType idType) {
		if (idType.hasVersionIdPart() && idType.hasIdPart()) {
			String fhirId = idType.getIdPart();
			Integer version = Integer.valueOf(idType.getVersionIdPart());
			if (version != null) {
				Query query = Query.query(
						Criteria.where(ConstantKeys.QP_FHIRID).is(fhirId).and(ConstantKeys.QP_VERSION).is(version));
				var entity = (ENT) mongo.findOne(query, getEntityClass());
				if (entity != null) {
					return transform(entity);
				}
			}
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<IBaseResource> getHistory(IdType theId, InstantType theSince, DateRangeParam theAt, NumberParam _page,
			Integer count) {
		List<IBaseResource> retVal = new ArrayList<>();
		Criteria criteria = null;
		criteria = Criteria.where("$where").is("1==1");
		if (theId != null && theId.hasIdPart()) {
			String fhirId = theId.getIdPart();
			criteria.and(ConstantKeys.QP_FHIRID).is(fhirId);
		}
		if (theAt != null) {
			criteria = DatabaseUtil.setTypeDateToCriteria(criteria, ConstantKeys.QP_UPDATED, theAt);
		}
		if (theSince != null) {
			Date dateParam = theSince.getValue();
			// criteria.and("resCreated").gte(dateParam).lte(dateNow);
			criteria.and(ConstantKeys.QP_UPDATED).gte(dateParam);
		}
		if (criteria != null) {
			Query qry = Query.query(criteria);
			if (_page != null && _page.getValue().intValue() > 0 && count > 0) {
				Pageable pageableRequest;
				pageableRequest = new PageRequest(
						_page != null ? Integer.valueOf(_page.getValue().intValue()) : ConstantKeys.PAGE,
						count != null ? count : ConstantKeys.DEFAULT_PAGE_SIZE);
				qry.with(pageableRequest);
			}

			qry.with(new Sort(Sort.Direction.DESC, ConstantKeys.QP_UPDATED));
			qry.with(new Sort(Sort.Direction.DESC, ConstantKeys.QP_CREATED));

			var result = mongo.find(qry, getEntityClass());
			for (var ent : result) {
				retVal.add(transform((ENT) ent));
			}
		}
		return retVal;
	}

	public int countHistory(IdType theId, InstantType theSince, DateRangeParam theAt) {
		int retVal = 0;
		Criteria criteria = null;
		criteria = Criteria.where("$where").is("1==1");
		if (theId != null && theId.hasIdPart()) {
			String fhirId = theId.getIdPart();
			criteria.and(ConstantKeys.QP_FHIRID).is(fhirId);
		}
		if (theAt != null) {
			criteria = DatabaseUtil.setTypeDateToCriteria(criteria, ConstantKeys.QP_UPDATED, theAt);
		}
		if (theSince != null) {
			Date dateParam = theSince.getValue();
			// criteria.and("resCreated").gte(dateParam).lte(dateNow);
			criteria.and(ConstantKeys.QP_UPDATED).gte(dateParam);
		}
		if (criteria != null) {
			Query qry = Query.query(criteria);

			retVal = (int) mongo.count(qry, getEntityClass());
		}
		return retVal;
	}

	@SuppressWarnings("unchecked")
	public List<FHIR> findByCriteria(Criteria criteria) {
		var query = Query.query(criteria);
		var lst = mongo.find(query, getEntityClass());
		return DataConvertUtil.transform(lst, x -> transform((ENT) x));
	}

	@SuppressWarnings("unchecked")
	public List<FHIR> searchResource(Criteria criteria, Boolean active, int start, int count, Sort sort) {
		if (active != null) {
			criteria.and(ConstantKeys.QP_ACTIVE).is(active);
		} else {
			criteria.and(ConstantKeys.QP_ACTIVE).is(true);
		}

		var query = Query.query(criteria);

		if (sort != null) {
			query.with(sort);
		} else {
			query.with(new Sort(Sort.Direction.DESC, ConstantKeys.QP_UPDATED));
			query.with(new Sort(Sort.Direction.DESC, ConstantKeys.QP_CREATED));
		}

		if (start >= 0)
			query.skip(start);
		if (count >= 0)
			query.limit(count);
		var lst = (List<ENT>) mongo.find(query, getEntityClass());
		return DataConvertUtil.transform(lst, x -> transform((ENT) x));
	}

	public List<FHIR> searchResource(Criteria criteria, int start, int count, Sort sort) {
		return searchResource(criteria, null, start, count, sort);
	}

	public List<FHIR> searchResource(Criteria criteria, int start, int count) {
		return searchResource(criteria, start, count, null);
	}

	public List<FHIR> searchResource(Criteria criteria) {
		return searchResource(criteria, -1, -1);
	}

	@SuppressWarnings("unchecked")
	public FHIR getResource(Criteria criteria) {
		criteria.and(ConstantKeys.QP_ACTIVE).is(true);
		var query = Query.query(criteria);
		var ent = (ENT) mongo.findOne(query, getEntityClass());
		return transform(ent);
	}

	public int countResource(Criteria criteria) {
		criteria.and(ConstantKeys.QP_ACTIVE).is(true);
		var query = Query.query(criteria);
		int count = (int) mongo.count(query, getEntityClass());
		return count;
	}
}
