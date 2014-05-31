package lt.core;

import static java.lang.System.out;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class Main {
  public static void main(String[] args) throws IOException {
    Directory dir = new RAMDirectory();
    //FSDirectory needs cleanup every time
    
    IndexWriter indexWriter = createWriter(dir);
    Documents documents = new Documents(Paths.get("test-data"));
    indexWriter.addDocuments(documents.get());
    indexWriter.close();
    
    IndexReader indexReader = createReader(dir);
    IndexSearcher searcher = new IndexSearcher(indexReader);
    TopDocs topDocs = searcher.search(new TermQuery(new Term("content", "fdklgkg")), 10);
    out.println("totalHits: " + topDocs.totalHits);
    for (ScoreDoc sd : topDocs.scoreDocs) {
      Document doc = searcher.doc(sd.doc);
      out.println(sd.doc+" "+doc);
    }
    indexReader.close();
  }

  private static IndexWriter createWriter(Directory dir) throws IOException {
    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);
    return new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_48, analyzer));
  }
  
  private static IndexReader createReader(Directory dir) {
    try {
      return DirectoryReader.open(dir);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
  
}
