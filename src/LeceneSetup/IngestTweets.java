package LeceneSetup;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.BaseDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

@WebServlet(name = "IngestTweets", urlPatterns = {"/query"})
public class IngestTweets extends HttpServlet {
    private Path indexDirectory = Paths.get("tweets_index");
    StandardAnalyzer analyzer;
    Directory directory;
    IndexWriter writer;

    private void indexDocument(Document document, StandardAnalyzer analyzer, IndexWriter writer) throws IOException {
        writer.addDocument(document);
    }
    private Document createDocument(String username, String text) {
        Document document = new Document();
        document.add(new TextField("username", username, Field.Store.YES));
        document.add(new TextField("text", text, Field.Store.YES));
        return document;
    }
    private void ingestTweets(IndexWriter writer, StandardAnalyzer analyzer) {
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader("/Users/samueldominguez/final-project-carriedbyjoker/1000_tweets.json")) {

            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            JSONArray tweets = (JSONArray) jsonObject.get("tweets");

            System.out.println(tweets);

            for (Object o : tweets) {
                JSONObject tweet = (JSONObject) o;
                String username = (String) tweet.get("username");
                String text = (String) tweet.get("text");
                Document d = createDocument(username, text);
                indexDocument(d, analyzer, writer);
            }
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            analyzer = new StandardAnalyzer();
            directory = new SimpleFSDirectory(indexDirectory);
            writer = new IndexWriter(directory, new IndexWriterConfig(analyzer).setOpenMode(IndexWriterConfig.OpenMode.CREATE));
            ingestTweets(writer, analyzer);

        } catch (Exception e) {
            e.printStackTrace();
        }

        String qs = request.getParameter("q");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        Query q;
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        try {
            q = new QueryParser("text", analyzer).parse(qs);
            ScoreDoc[] hits = submitQuery(q, reader, searcher);
            out.println("<html>");
            out.println("<body>");
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                System.out.println(d.get("username"));
                out.println("<br> <br>");
                out.println("User: " + d.get("username") + " says: " + d.get("text"));

            }
            out.println("</body>");
            out.println("</html>");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ScoreDoc[] submitQuery(Query q, IndexReader reader, IndexSearcher searcher) throws IOException{
        int hitsPerPage = 10;
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;
        return hits;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


    }
}
