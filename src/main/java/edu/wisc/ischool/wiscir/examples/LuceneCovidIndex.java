package edu.wisc.ischool.wiscir.examples;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class LuceneCovidIndex {

    public static void main(String[] args) throws IOException {
        //1、创建一个Directory对象，指定索引库保存的位置。
        Directory directory = FSDirectory.open(new File("indexCovid").toPath());
        //new IndexWriterConfig() 默认使用的是标准分析器new StandardAnalyzer()
        //IndexWriterConfig conf = new IndexWriterConfig();
        //此处可以自定义分析器 如使用ik分析器
        Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents( String fieldName ) {
                // Step 1: tokenization (Lucene's StandardTokenizer is suitable for most text retrieval occasions)
                TokenStreamComponents ts = new TokenStreamComponents( new StandardTokenizer() );
                // Step 2: transforming all tokens into lowercased ones (recommended for the majority of the problems)
                ts = new TokenStreamComponents( ts.getSource(), new LowerCaseFilter( ts.getTokenStream() ) );
                // Step 3: whether to remove stop words (unnecessary to remove stop words unless you can't afford the extra disk space)
                // Uncomment the following line to remove stop words
                // ts = new TokenStreamComponents( ts.getSource(), new StopFilter( ts.getTokenStream(), EnglishAnalyzer.ENGLISH_STOP_WORDS_SET ) );
                // Step 4: whether to apply stemming
                // Uncomment one of the following two lines to apply Krovetz or Porter stemmer (Krovetz is more common for IR research)
                ts = new TokenStreamComponents( ts.getSource(), new PorterStemFilter( ts.getTokenStream() ) );
                // ts = new TokenStreamComponents( ts.getSource(), new PorterStemFilter( ts.getTokenStream() ) );
                return ts;
            }
        };

        IndexWriterConfig conf = new IndexWriterConfig(analyzer);
        // Note that IndexWriterConfig.OpenMode.CREATE will override the original index in the folder
        conf.setOpenMode( IndexWriterConfig.OpenMode.CREATE );
        // Lucene's default BM25Similarity stores document field length using a "low-precision" method.
        // Use the BM25SimilarityOriginal to store the original document length values in index.
        conf.setSimilarity( new BM25SimilarityOriginal() );

        //2、基于Directory对象创建一个IndexWriter对象
        IndexWriter indexWriter = new IndexWriter(directory, conf);

        FieldType fieldTypeMetadata = new FieldType();
        fieldTypeMetadata.setOmitNorms( true );
        fieldTypeMetadata.setIndexOptions( IndexOptions.DOCS );
        fieldTypeMetadata.setStored( true );
        fieldTypeMetadata.setTokenized( false );
        fieldTypeMetadata.freeze();

        FieldType fieldTypeText = new FieldType();
        fieldTypeText.setIndexOptions( IndexOptions.DOCS_AND_FREQS_AND_POSITIONS );
        fieldTypeText.setStoreTermVectors( true );
        fieldTypeText.setStoreTermVectorPositions( true );
        fieldTypeText.setTokenized( true );
        fieldTypeText.setStored( true );
        fieldTypeText.freeze();
        //3、读取磁盘上的文件，对应每个文件创建一个文档对象。
        File dir = new File("covid_dataset");
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            //文件名
            String fileName = file.getName();
            //文件内容
            String fileContent = FileUtils.readFileToString(file, "utf-8");
            //文件路径
            String filePath = file.getPath();
            //文件大小
            long fileSize = FileUtils.sizeOf(file);

            //创建文件名域
            /*
             * 三个参数的含义
             *      String name 域的名称
             *      String value 域的内容
             *      Store store 是否存储
             */
            //创建document对象
            Document document = new Document();
            //向文档对象中添加域
            document.add( new Field( "fileName", fileName, fieldTypeMetadata ) );
            document.add( new Field( "fileContent", fileContent, fieldTypeText ) );
//            document.add( new Field( "filePath", filePath, fieldTypeText ) );
//            document.add( new Field( "fileSize", fileSize+"", fieldTypeText ) );
            //4.把文档对象写入索引库
            indexWriter.addDocument(document);
        }
        //5.关闭indexWriter对象
        indexWriter.close();


    }

}
