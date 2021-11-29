package com.example.processor;

import org.springframework.batch.item.ItemProcessor;

import com.example.domain.ReportConfig;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ReportConfigItemProcessor implements ItemProcessor<ReportConfig, ReportConfig> {

	@Override
	public ReportConfig process(ReportConfig item) throws Exception {
		
		item.setIndex(item.getIndex() + 3);
		
		log.debug("item={}", item);
		
		return item;
	}
}
