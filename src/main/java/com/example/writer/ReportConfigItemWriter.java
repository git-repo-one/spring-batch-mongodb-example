package com.example.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.example.domain.ReportConfig;

import lombok.Builder;
import lombok.extern.log4j.Log4j2;

@Builder
@Log4j2
public class ReportConfigItemWriter implements ItemWriter<ReportConfig> {

	private final MongoTemplate mongoTemplate;

	@Override
	public void write(List<? extends ReportConfig> items) throws Exception {

		log.debug("items={}", items);
		
		BulkOperations bulkOperations = this.mongoTemplate.bulkOps(BulkMode.ORDERED, ReportConfig.class);
		
		for (ReportConfig reportConfig : items) {
			
			Criteria criteria = Criteria.where("_id").is(reportConfig.getId()).andOperator(
					Criteria.where("updateHash").is(reportConfig.getUpdateHash()));

			Query query = Query.query(criteria);

			Update update = Update.update("index", reportConfig.getIndex());
			
			bulkOperations.updateOne(query, update);
		}
		
		bulkOperations.execute();
	}
}
