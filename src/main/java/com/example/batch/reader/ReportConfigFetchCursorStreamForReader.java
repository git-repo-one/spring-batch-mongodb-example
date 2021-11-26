package com.example.batch.reader;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregationOptions;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.stream.Stream;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import com.example.batch.infra.reader.FetchCursorStreamForReader;
import com.example.domain.ReportConfig;

import lombok.Builder;

@Builder
public class ReportConfigFetchCursorStreamForReader implements FetchCursorStreamForReader<ReportConfig> {

	private final MongoTemplate mongoTemplate;
	private final int batchSize;

	@Override
	public Stream<ReportConfig> fetch() {

		Aggregation agg = newAggregation(
					match(Criteria.where("index").lt(30).andOperator(Criteria.where("index").gt(9))),
					sort(Sort.Direction.ASC, "index")
				)
				.withOptions(newAggregationOptions().cursorBatchSize(batchSize).build());

		return mongoTemplate.aggregateStream(agg, ReportConfig.class, ReportConfig.class).stream();
	}
}
