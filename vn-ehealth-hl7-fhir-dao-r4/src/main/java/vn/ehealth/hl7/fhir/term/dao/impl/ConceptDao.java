package vn.ehealth.hl7.fhir.term.dao.impl;

import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirVersionEnum;
import vn.ehealth.hl7.fhir.core.util.ConstantKeys;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.dao.CachingConfiguration;
import vn.ehealth.hl7.fhir.term.entity.ConceptEntity;
import vn.ehealth.utils.MongoUtils;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@Repository
public class ConceptDao  {

	@Autowired
	private MongoOperations mongo;
	
	public ConceptEntity create(ConceptEntity ent) throws Exception {
		if (ent != null) {
			ent._fhirId = StringUtil.generateUUID();
			ent._version = ConstantKeys.VERSION_1;
			ent._active = (true);
			ent._resCreated = new Date();
			ent._resUpdated = new Date();
			ent._fhirVersion = FhirVersionEnum.R4.toString();
			ent.status = PublicationStatus.ACTIVE.toCode();
			ent.date = new Date();
			return mongo.save(ent);
		}
		return null;
	}

	@CachePut(cacheResolver = CachingConfiguration.CACHE_RESOLVER_NAME, key = "#idType", condition = "#idType!=null")
	public ConceptEntity update(ConceptEntity ent, IdType idType) throws Exception {
		
		if (idType != null && idType.hasIdPart()) {
			var entityOld = read(idType.getIdPart());
		
			if (entityOld != null) {
			
				// remove old entity
				entityOld._resDeleted = new Date();
				entityOld._active = false;
				mongo.save(entityOld);
	
				// save new entity
				if (ent != null) {
					ent._fhirId = entityOld._fhirId;
					ent._version = entityOld._version + 1;
					ent._active = true;
					ent._resCreated = new Date();
					ent._resUpdated = new Date();
					ent._fhirVersion = FhirVersionEnum.R4.toString();
					return mongo.save(ent);
				}
			}
		}
		
		return null;
	}

	@Cacheable(value="Concept", key = "#id")
	public ConceptEntity read(String id) {
		var params = mapOf(ConstantKeys.QP_FHIRID, (Object) id, ConstantKeys.QP_ACTIVE , true);
		var criteria = MongoUtils.createCriteria(params);
		var query = Query.query(criteria);
		return mongo.findOne(query, ConceptEntity.class);
	}

	@CacheEvict(value="Concept", key = "#id")
	public ConceptEntity remove(String id) {
		var entity = read(id);
		
		if (entity != null) {
			entity._active = (false);
			entity._resDeleted = (new Date());
			return mongo.save(entity);
		}
		return null;
	}

	public List<ConceptEntity> getByCodeSystem(String codeSystemId) {
		var params = mapOf("codeSystemId", (Object) codeSystemId, ConstantKeys.QP_ACTIVE , true);
		var criteria = MongoUtils.createCriteria(params);
		var query = Query.query(criteria);
		return mongo.find(query, ConceptEntity.class);
	}
	
	public List<ConceptEntity> searchEntity(Criteria criteria, Boolean active, int start, int count, Sort sort) {
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
		return mongo.find(query, ConceptEntity.class);
	}
	
	public List<ConceptEntity> searchEntity(Criteria criteria, int start, int count, Sort sort) {
		return searchEntity(criteria, null, start, count, sort);
	}

	public List<ConceptEntity> searchEntity(Criteria criteria, int start, int count) {
		return searchEntity(criteria, start, count, null);
	}

	public List<ConceptEntity> searchEntity(Criteria criteria) {
		return searchEntity(criteria, -1, -1);
	}

	public ConceptEntity getEntity(Criteria criteria) {
		criteria.and(ConstantKeys.QP_ACTIVE).is(true);
		var query = Query.query(criteria);
		return mongo.findOne(query, ConceptEntity.class);
	}

	public int countEntity(Criteria criteria) {
		criteria.and(ConstantKeys.QP_ACTIVE).is(true);
		var query = Query.query(criteria);
		return (int) mongo.count(query, ConceptEntity.class);
	}
}
