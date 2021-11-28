package com.example.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.example.domain.ReportConfig;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ReportConfigRepositoryCustomImpl implements ReportConfigRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void update(ReportConfig reportConfig) {

		Criteria criteria = Criteria.where("_id").is(reportConfig.getId()).andOperator(
				Criteria.where("updateHash").is(reportConfig.getUpdateHash()));

		Query query = Query.query(criteria);

		Update update = Update.update("index", reportConfig.getIndex());

		//FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().returnNew(true);
		FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.none();

		//ReportConfig updatedReportConfig = mongoTemplate.findAndModify(query, update, findAndModifyOptions, ReportConfig.class);
		ReportConfig updatedReportConfig = mongoTemplate.findAndModify(query, update, ReportConfig.class);

		log.debug("updatedReportConfig={}", updatedReportConfig);
	}
}
