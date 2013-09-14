/*
 *  This software illustrates the architecture for the portion of a
 *  search engine that evaluates queries.  It is a template for class
 *  homework assignments, so it emphasizes simplicity over efficiency.
 *  It implements an unranked Boolean retrieval model, however it is
 *  easily extended to other retrieval models.  For more information,
 *  see the ReadMe.txt file.
 *
 *  Copyright (c) 2013, Carnegie Mellon University.  All Rights Reserved.
 */

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;




public class QryEval {

  static String usage = "Usage:  java " + System.getProperty("sun.java.command")
      + " paramFile\n\n";

  /**
   * The index file reader is accessible via a global variable. This isn't great programming style,
   * but the alternative is for every query operator to store or pass this value, which creates its
   * own headaches.
   */
  public static IndexReader READER;
  
  //Define retrieval mode;
  public enum Mode {UNRANKED_BOOLEAN, RANKED_BOOLEAN};

  public static EnglishAnalyzerConfigurable analyzer =  new EnglishAnalyzerConfigurable (Version.LUCENE_43);
  static {
    analyzer.setLowercase(true);
    analyzer.setStopwordRemoval(true);
    analyzer.setStemmer(EnglishAnalyzerConfigurable.StemmerType.KSTEM);
  }

  /**
   * 
   * @param args The only argument should be one file name, which indicates the parameter file.
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    
    // must supply parameter file
    if (args.length < 1) {
      System.err.println(usage);
      System.exit(1);
    }
    //initialise mode to Unranked boolean
    Mode mode = Mode.UNRANKED_BOOLEAN;
    // read in the parameter file; one parameter per line in format of key=value
    Map<String, String> params = new HashMap<String, String>();
    Scanner scan = new Scanner(new File(args[0]));
    String line = null;
    do {
      line = scan.nextLine();
      String[] pair = line.split("=");
      params.put(pair[0].trim(), pair[1].trim());
    } while (scan.hasNext());
    
    // parameters required for this example to run
    if (!params.containsKey("indexPath")) {
      System.err.println("Error: Parameters were missing.");
      System.exit(1);
    }

    // open the index
    READER = DirectoryReader.open(FSDirectory.open(new File(params.get("indexPath"))));

    if (READER == null) {
      System.err.println(usage);
      System.exit(1);
    }
    
    //Get mode (ranked,unranked boolean or other algo in the future)
    if(!params.containsKey("retrievalAlgorithm"))
    {
      System.err.println("No specify retrievalAlgorithm in parameter.txt, use Unranked Boolean Mode");
      
    }
    else
    {
      String modeStr = (params.get("retrievalAlgorithm"));
      if(modeStr.equalsIgnoreCase("UnrankedBoolean"))
      {
        mode = Mode.UNRANKED_BOOLEAN;
      }
      else if(modeStr.equalsIgnoreCase("RankedBoolean"))
      {
        mode = Mode.RANKED_BOOLEAN;
      }
    }

    //Read query from file 
    if(!params.containsKey("queryFilePath"))
    {
      System.err.println("Pls specify queryFilePath in parameter.txt");
      System.exit(1);
    }
    
 // String[] test = tokenizeQuery("#AND (aparagus.title broccoli cauliflower #SYN(peapods peas))");
    
    Scanner scanQuery = new Scanner(new File(params.get("queryFilePath")));
    do {
      line = scanQuery .nextLine();
      int j = line.indexOf(":");
      String queryID = line.substring(0,j).trim();
      //Parse and Evaluate Result
      line = line.substring(j+1);
      doit(line,mode,queryID);
      
      
      
    } while (scanQuery.hasNext());
    
   
    
    // The index is open. Start evaluating queries. The examples below show the query tree that
    // should be created for each query.
    // 
    // Modify me so that you read queries from a file, parse it, and form the query tree
    // automatically.
    //String[] test = tokenizeQuery("#AND (aparagus broccoli cauliflower #SYN(peapods peas))");
    /*
    printResults("#AND (aparagus broccoli cauliflower #SYN(peapods peas))", (new QryopAnd(
        new QryopTerm(tokenizeQuery("asparagus")[0]),
        new QryopTerm(tokenizeQuery("broccoli")[0]),
        new QryopTerm(tokenizeQuery("cauliflower")[0]),
        new QryopSyn(
            new QryopTerm(tokenizeQuery("peapods")[0]), 
            new QryopTerm(tokenizeQuery("peas")[0])))).evaluate());

    printResults("asparagus", (new QryopScore(
        new QryopTerm(tokenizeQuery("asparagus")[0]))).evaluate());

    printResults("broccoli", (new QryopScore(
        new QryopTerm(tokenizeQuery("broccoli")[0]))).evaluate());

    printResults("cauliflower", (new QryopScore(
        new QryopTerm(tokenizeQuery("cauliflower")[0]))).evaluate());

    printResults("pea", (new QryopScore(
        new QryopTerm(tokenizeQuery("pea")[0]))).evaluate());

    printResults("peas", (new QryopScore(
        new QryopTerm(tokenizeQuery("peas")[0]))).evaluate());

    printResults("peapods", (new QryopScore(
        new QryopTerm(tokenizeQuery("peapods")[0]))).evaluate());

    printResults("#AND (broccoli cauliflower)", (new QryopAnd(
        new QryopTerm(tokenizeQuery("broccoli")[0]),
        new QryopTerm(tokenizeQuery("cauliflower")[0]))).evaluate());

    printResults("#AND (peas peapods)", (new QryopAnd(
        new QryopTerm(tokenizeQuery("peas")[0]), 
        new QryopTerm(tokenizeQuery("peapods")[0]))).evaluate());

    printResults("#SCORE (#SCORE (#AND (peas peapods)))",
       (new QryopScore(
        (new QryopScore(
	   (new QryopAnd(
	      new QryopTerm(tokenizeQuery("peas")[0]), 
	      new QryopTerm(tokenizeQuery("peapods")[0]))))))).evaluate());
*/
  }

  private static void doit(String line, Mode mode, String queryId) {
    // TODO Auto-generated method stub
    TreeNode root = new TreeNode();
    if(line.indexOf("#")<0)
    {
      root.setType(TreeNode.Type.OR);
    }
    root.setChildren(root.MakeChildren(line));
    QryResult result = root.eval(mode);
    try {
      Collections.sort(result.docScores.scores,new DocScoreComparator());
      printResults(queryId,result);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   *  Get the external document id for a document specified by an
   *  internal document id.  Ordinarily this would be a simple call to
   *  the Lucene index reader, but when the index was built, the
   *  indexer added "_0" to the end of each external document id.  The
   *  correct solution would be to fix the index, but it's too late
   *  for that now, so it is fixed here before the id is returned.
   * 
   * @param iid The internal document id of the document.
   * @throws IOException 
   */
  static String getExternalDocid (int iid) throws IOException {
    Document d = QryEval.READER.document (iid);
    String eid = d.get ("externalId");

    if ((eid != null) && eid.endsWith ("_0"))
      eid = eid.substring (0, eid.length()-2);

    return (eid);
  }

  /**
   * Prints the query results. 
   * 
   * THIS IS NOT THE CORRECT OUTPUT FORMAT.
   * YOU MUST CHANGE THIS METHOD SO THAT IT OUTPUTS IN THE FORMAT SPECIFIED IN THE HOMEWORK PAGE, 
   * WHICH IS: 
   * 
   * QueryID Q0 DocID Rank Score RunID
   * 
   * @param queryName Original query.
   * @param result Result object generated by {@link Qryop#evaluate()}.
   * @throws IOException 
   */
  static void printResults(String queryName, QryResult result) throws IOException {
    /*
    System.out.println(queryName + ":  ");
    if (result.docScores.scores.size() < 1) {
      System.out.println("\tNo results.");
    } else {
      for (int i = 0; i < result.docScores.scores.size(); i++) {
        System.out.println("\t" + i + ":  "
			   + getExternalDocid (result.docScores.getDocid(i))
			   + ", "
			   + result.docScores.getDocidScore(i));
      }
    }
    */
    
    for (int i = 0; i < result.docScores.scores.size() && i<100; i++) {
      System.out.println(queryName+"\tQ0\t" + 
      getExternalDocid (result.docScores.getDocid(i)) +"\t"+
       (i+1) + "\t"  
       + result.docScores.getDocidScore(i) + "\trun_01");
    }
  }

  /**
   * Given a query string, returns the terms one at a time with stopwords
   * removed and the terms stemmed using the Krovetz stemmer. 
   * 
   * Use this method to process raw query terms. 
   * 
   * @param query String containing query
   * @return Array of query tokens
   * @throws IOException
   */
  static String[] tokenizeQuery(String query) throws IOException {
    
    TokenStreamComponents comp = analyzer.createComponents("dummy", new StringReader(query));
    TokenStream tokenStream = comp.getTokenStream();

    CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
    tokenStream.reset();
    
    List<String> tokens = new ArrayList<String>();
    while (tokenStream.incrementToken()) {
      String term = charTermAttribute.toString();
      tokens.add(term);
    }
    return tokens.toArray(new String[tokens.size()]);
  }

}
