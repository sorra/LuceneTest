package lt.core;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;

public class Documents {
  private List<Document> docs = new ArrayList<>();
  
  public Documents(Path src) {
    readinDocs(src);
  }
  
  public List<Document> get() {
    return Collections.unmodifiableList(docs);
  }
  
  private void readinDocs(Path src) {
    try {
      List<String> lines = Files.readAllLines(src);
      
      Pos lastPos = null;
      DocParts parts = new DocParts();
      for (int i = 0; i< lines.size(); i++) {
        String curln = lines.get(i);
        if (curln.equals("###```")) {
          docs.add(createDoc(parts));
          parts = new DocParts();
          lastPos = Pos.END;
          
        } else if (lastPos == null || lastPos == Pos.END) {
          parts.id = Long.parseLong(curln);
          lastPos = Pos.ID;
          
        } else if (lastPos == Pos.ID) {
          parts.title = curln;
          lastPos = Pos.TITLE;
          
        } else if (lastPos == Pos.TITLE || lastPos == Pos.CONTENT) {
          if (parts.content.length() > 0) {
            parts.content.append('\n');
          }
          parts.content.append(curln);
          lastPos = Pos.CONTENT;
        }
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
  
  private Document createDoc(DocParts parts) {
    Long id = parts.id;
    String title = parts.title;
    String content = parts.content.toString();
    Objects.requireNonNull(id);
    Objects.requireNonNull(title);
    System.out.printf("%d\n%s\n%s\n\n", id, title, content);
    
    Document doc = new Document();
    doc.add(new LongField("id", id, Store.YES));
    doc.add(new TextField("title", title, Store.YES));
    doc.add(new TextField("content", content, Store.YES));
    return doc;
  }
  
  private static class DocParts {
    Long id;
    String title;
    StringBuilder content = new StringBuilder(100);
  }
  
  private static enum Pos {
    ID, TITLE, CONTENT, END
  }
}
