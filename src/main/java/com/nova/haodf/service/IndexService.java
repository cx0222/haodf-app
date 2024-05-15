package com.nova.haodf.service;

import com.nova.haodf.config.LuceneConfig;
import com.nova.haodf.entity.Doctor;
import com.nova.haodf.entity.Hospital;
import com.nova.haodf.exception.QueryException;
import com.nova.haodf.repository.DoctorRepository;
import com.nova.haodf.repository.HospitalRepository;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IndexService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexService.class);
    private static final String[] HOSPITAL_FIELDS = new String[]{
            "hospitalId", "name", "provinceName", "introduction", "address", "addressDetail", "categoryDescription", "gradeDescription", "propertyDescription"};
    private static final String[] DOCTOR_FIELDS = new String[]{
            "doctorId", "name", "title", "grade", "facultyName", "hospitalFacultyName", "educationGrade", "specialization"
    };
    private final LuceneConfig luceneConfig;
    private final HospitalRepository hospitalRepository;
    private final DoctorRepository doctorRepository;

    public IndexService(LuceneConfig luceneConfig, HospitalRepository hospitalRepository, DoctorRepository doctorRepository) {
        this.luceneConfig = luceneConfig;
        this.hospitalRepository = hospitalRepository;
        this.doctorRepository = doctorRepository;
    }

    private static String trimString(String original) {
        if (original == null) {
            return "";
        }
        String result = original.replaceAll("[ \n\r\t<>\"]", " ");
        result = result.replaceAll(" +", " ");
        if (result.length() > 30000) {
            result = result.substring(0, 30000);
        }
        return result;
    }

    public long indexAllHospitals(IndexWriter indexWriter) throws IOException {
        List<Hospital> hospitalList = hospitalRepository.findAll();
        long indexedDocumentCount = 0;
        for (Hospital hospital : hospitalList) {
            try {
                Document document = new Document();
                document.add(new LongField(
                        "hospitalId", hospital.getHospitalId(), Field.Store.YES
                ));
                document.add(new TextField(
                        "name", trimString(hospital.getName()), Field.Store.YES
                ));
                document.add(new TextField(
                        "provinceName", trimString(hospital.getProvinceName()), Field.Store.YES
                ));
                document.add(new TextField(
                        "introduction", trimString(hospital.getIntroduction()), Field.Store.YES
                ));
                document.add(new TextField(
                        "address", trimString(hospital.getAddress()), Field.Store.YES
                ));
                document.add(new TextField(
                        "addressDetail", trimString(hospital.getAddressDetail()), Field.Store.YES
                ));
                document.add(new TextField(
                        "categoryDescription", trimString(hospital.getCategoryDescription()), Field.Store.YES
                ));
                document.add(new TextField(
                        "gradeDescription", trimString(hospital.getGradeDescription()), Field.Store.YES
                ));
                document.add(new TextField(
                        "propertyDescription", trimString(hospital.getPropertyDescription()), Field.Store.YES
                ));
                indexWriter.addDocument(document);
                ++indexedDocumentCount;
                LOGGER.info("Current indexed document count = {}", indexedDocumentCount);
            } catch (Exception exception) {
                LOGGER.warn("Document failed to be indexed", exception);
            }
        }
        indexWriter.close();
        return indexedDocumentCount;
    }

    public long indexAllDoctors(IndexWriter indexWriter) throws IOException {
        List<Doctor> doctorList = doctorRepository.findAll();
        long indexedDocumentCount = 0;
        for (Doctor doctor : doctorList) {
            try {
                Document document = new Document();
                document.add(new LongField(
                        "doctorId", doctor.getDoctorId(), Field.Store.YES
                ));
                document.add(new TextField(
                        "name", trimString(doctor.getName()), Field.Store.YES
                ));
                document.add(new TextField(
                        "doctorIntroduction", trimString(doctor.getDoctorIntroduction()), Field.Store.YES
                ));
                document.add(new TextField(
                        "title", trimString(doctor.getTitle()), Field.Store.YES
                ));
                document.add(new TextField(
                        "grade", trimString(doctor.getGrade()), Field.Store.YES
                ));
                document.add(new TextField(
                        "facultyName", trimString(doctor.getFacultyName()), Field.Store.YES
                ));
                document.add(new TextField(
                        "hospitalFacultyName", trimString(doctor.getHospitalFacultyName()), Field.Store.YES
                ));
                document.add(new TextField(
                        "educationGrade", trimString(doctor.getEducationGrade()), Field.Store.YES
                ));
                document.add(new TextField(
                        "specialization", trimString(doctor.getSpecialization()), Field.Store.YES
                ));
                indexWriter.addDocument(document);
                ++indexedDocumentCount;
                LOGGER.info("Current indexed document count = {}", indexedDocumentCount);
            } catch (Exception exception) {
                LOGGER.warn("Document failed to be indexed", exception);
            }
        }
        indexWriter.close();
        return indexedDocumentCount;
    }

    public List<Map<String, String>> searchFromIndex(String queryString, int maxDocumentCount, Path indexPath, String[] fields) throws IOException, ParseException {
        List<Map<String, String>> mapList = new ArrayList<>();
        try (Directory index = FSDirectory.open(indexPath)) {
            IndexReader reader = DirectoryReader.open(index);
            IndexSearcher searcher = new IndexSearcher(reader);
            try (Analyzer analyzer = new IKAnalyzer()) {
                QueryParser parser = new MultiFieldQueryParser(fields, analyzer);
                Query query = parser.parse(queryString);
                QueryScorer scorer = new QueryScorer(query);
                SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span>", "</span>");
                Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
                Highlighter highlighter = new Highlighter(formatter, scorer);
                highlighter.setTextFragmenter(fragmenter);

                LOGGER.info("{} - {}", query, queryString);
                TopDocs topDocs = searcher.search(query, maxDocumentCount);
                ScoreDoc[] scoreDocs = topDocs.scoreDocs;
                for (ScoreDoc scoreDoc : scoreDocs) {
                    int doc = scoreDoc.doc;
                    double score = scoreDoc.score;
                    Document document = searcher.doc(doc);
                    List<IndexableField> fieldList = document.getFields();
                    Map<String, String> fieldMap = new HashMap<>();
                    fieldMap.put("score", String.valueOf(score));
                    for (IndexableField field : fieldList) {
                        String highlightedField = "";
                        try {
                            highlightedField = getHighlightedField(document, reader, doc, field, highlighter, analyzer);
                        } catch (InvalidTokenOffsetsException exception) {
                            LOGGER.warn("?", exception);
                        } finally {
                            fieldMap.put(field.name(), highlightedField);
                        }
                    }
                    mapList.add(fieldMap);
                }
            }
        }
        return mapList;
    }

    public List<Map<String, String>> searchAllHospitals(String queryString, int maxDocumentCount) {
        try {
            return searchFromIndex(queryString, maxDocumentCount, luceneConfig.getHospitalIndexPath(), HOSPITAL_FIELDS);
        } catch (IOException | ParseException exception) {
            throw new QueryException(exception);
        }
    }

    public List<Map<String, String>> searchAllDoctors(String queryString, int maxDocumentCount) {
        try {
            return searchFromIndex(queryString, maxDocumentCount, luceneConfig.getDoctorIndexPath(), DOCTOR_FIELDS);
        } catch (IOException | ParseException exception) {
            throw new QueryException(exception);
        }
    }

    private String getHighlightedField(
            Document document, IndexReader reader, int doc, IndexableField field, Highlighter highlighter, Analyzer analyzer
    ) throws IOException, InvalidTokenOffsetsException {
        String content = field.stringValue();
        String result = null;
        if (content != null && !content.isEmpty()) {
            try (TokenStream tokenStream = TokenSources.getAnyTokenStream(reader, doc, field.name(), document, analyzer)) {
                String[] fragments = highlighter.getBestFragments(tokenStream, content, 3);
                result = String.join(" ", fragments);
            }
        }
        if (result == null || result.isEmpty()) {
            result = content;
        }
        return result;
    }
}
