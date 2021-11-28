package com.example.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.domain.ReportConfig;

public interface ReportConfigRepository extends MongoRepository<ReportConfig, String>, ReportConfigRepositoryCustom {

}
