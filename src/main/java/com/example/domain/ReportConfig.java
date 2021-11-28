package com.example.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection="report_config")
public class ReportConfig {

	@Id
	private String id;

	private String text;
	private int index;
	
	private String updateHash;
}
