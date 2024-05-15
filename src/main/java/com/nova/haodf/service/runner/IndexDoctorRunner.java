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
public class IndexDoctorRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexDoctorRunner.class);
    private final LuceneConfig luceneConfig;
    private final IndexService indexService;
    @Value("${options.runner.index-doctor}")
    private Boolean taskOn;

    @Autowired
    public IndexDoctorRunner(LuceneConfig luceneConfig, IndexService indexService) {
        this.luceneConfig = luceneConfig;
        this.indexService = indexService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!taskOn) {
            LOGGER.warn("Index doctor runner is not started.");
            return;
        }
        try (Directory directory = FSDirectory.open(luceneConfig.getDoctorIndexPath())) {
            try (Analyzer analyzer = new IKAnalyzer()) {
                IndexWriterConfig config = new IndexWriterConfig(analyzer);
                IndexWriter indexWriter = new IndexWriter(directory, config);
                long documentCount = indexService.indexAllDoctors(indexWriter);
                LOGGER.info("Indexed {} doctors", documentCount);
            }
        }
    }
}
