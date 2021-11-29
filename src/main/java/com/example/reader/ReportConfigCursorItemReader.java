package com.example.reader;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregationOptions;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.batch.item.data.AbstractPaginatedDataItemReader;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.ClassUtils;

import com.example.domain.ReportConfig;

public class ReportConfigCursorItemReader extends AbstractPaginatedDataItemReader<ReportConfig> {

	private MongoTemplate mongoTemplate;
	private int batchSize;

	private Stream<ReportConfig> cursorStream;
	private Iterator<ReportConfig> cursorIterator;

	public ReportConfigCursorItemReader() {
		super();
		setName(ClassUtils.getShortName(ReportConfigCursorItemReader.class));
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	@Override
	protected void doOpen() throws Exception {

		cursorStream = fetchCursorStream();
		cursorIterator = cursorStream.iterator();
	}

	@Override
	protected void doClose() throws Exception {
		cursorStream.close();
	}

	@Override
	protected Iterator<ReportConfig> doPageRead() {

		List<ReportConfig> pageOfItems = new ArrayList<>();

		int i = 0;

		while (cursorIterator.hasNext()) {

			++i;

			ReportConfig reportConfig = cursorIterator.next();

			pageOfItems.add(reportConfig);

			if (i == pageSize) {

				return pageOfItems.iterator();
			}
		}

		return pageOfItems.iterator();
	}

	private Stream<ReportConfig> fetchCursorStream() {

		Aggregation agg = newAggregation(
				match(Criteria.where("index").lt(30).andOperator(Criteria.where("index").gt(9))),
				sort(Sort.Direction.ASC, "index")
				)
				.withOptions(newAggregationOptions().cursorBatchSize(batchSize).build());

		return mongoTemplate.aggregateStream(agg, ReportConfig.class, ReportConfig.class).stream();
	}
}
