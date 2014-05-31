package lt.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Main {
  public static void main(String[] args) throws IOException {
    FSDirectory dir = FSDirectory.open(new File("F:/lucene-index"));
    
    IndexWriter indexWriter = createWriter(dir);
    Documents documents = new Documents(Paths.get("test-data"));
    indexWriter.addDocuments(documents.get());
    indexWriter.close();
  }

  private static IndexWriter createWriter(FSDirectory dir) throws IOException {
    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);
    return new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_48, analyzer));
  }
  
}
