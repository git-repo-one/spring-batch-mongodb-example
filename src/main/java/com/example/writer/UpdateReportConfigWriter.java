package com.example.writer;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.example.domain.ReportConfig;
import com.example.repository.ReportConfigRepository;

import lombok.Builder;
import lombok.extern.log4j.Log4j2;

@Builder
@Log4j2
public class UpdateReportConfigWriter implements ItemWriter<ReportConfig> {

	private final ReportConfigRepository reportConfigRepository;

	@Override
	public void write(List<? extends ReportConfig> items) throws Exception {

		log.debug("items={}", items);
		
		items.forEach(item -> {
			reportConfigRepository.update(item);
		});
	}
	/*
	private void saveOrUpdate(List<? extends T> items) {
		BulkOperations bulkOperations = initBulkOperations(BulkMode.ORDERED, items.get(0));
		MongoConverter mongoConverter = this.template.getConverter();
		FindAndReplaceOptions upsert = new FindAndReplaceOptions().upsert();
		for (Object item : items) {
			Document document = new Document();
			mongoConverter.write(item, document);
			Object objectId = document.get(ID_KEY) != null ? document.get(ID_KEY) : new ObjectId();
			Query query = new Query().addCriteria(Criteria.where(ID_KEY).is(objectId));
			bulkOperations.replaceOne(query, document, upsert);
		}
		bulkOperations.execute();
	}

	private BulkOperations initBulkOperations(BulkMode bulkMode, Object item) {
		BulkOperations bulkOperations;
		if (StringUtils.hasText(this.collection)) {
			bulkOperations = this.template.bulkOps(bulkMode, this.collection);
		}
		else {
			bulkOperations = this.template.bulkOps(bulkMode, ClassUtils.getUserClass(item));
		}
		return bulkOperations;
	}
	*/
}
