package com.nova.haodf.service.runner;

import com.nova.haodf.config.LuceneConfig;
import com.nova.haodf.service.IndexService;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.wltea.analyzer.lucene.IKAnalyzer;

@Component
public class IndexHospitalRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexHospitalRunner.class);
    private final LuceneConfig luceneConfig;
    private final IndexService indexService;
    @Value("${options.runner.index-hospital}")
    private Boolean taskOn;

    @Autowired
    public IndexHospitalRunner(LuceneConfig luceneConfig, IndexService indexService) {
        this.luceneConfig = luceneConfig;
        this.indexService = indexService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!taskOn) {
            LOGGER.warn("Index hospital runner is not started.");
            return;
        }
        try (Directory directory = FSDirectory.open(luceneConfig.getHospitalIndexPath())) {
            try (Analyzer analyzer = new IKAnalyzer()) {
                IndexWriterConfig config = new IndexWriterConfig(analyzer);
                IndexWriter indexWriter = new IndexWriter(directory, config);
                long documentCount = indexService.indexAllHospitals(indexWriter);
                LOGGER.info("Indexed {} hospitals", documentCount);
            }
        }
    }
}
