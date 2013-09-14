import java.io.IOException;
import java.util.Vector;



public class QryopNear extends Qryop {

  private int n;
  
  public QryopNear(int n) {
    this.n = n;
  }

  /**
   * Evaluate the query operator.
   */
  public QryResult evaluate() throws IOException {

    InvList list1 =  args.get(0).evaluate().invertedList;
    InvList list2 =  args.get(1).evaluate().invertedList;
    
    QryResult result = new QryResult();
    int i1Doc = 0; /* Index of a document in list1. */
    int i2Doc = 0; /* Index of a document in list2. */
    int df = 0;
    int ctf = 0;
    // Each pass of the loop inserts one docPosting from i2 into i1.
    while (i2Doc < list2.df) {

      // Skip documents in i1 that are less than the current document in i2.
      while ((i1Doc < list1.df)
          && (list1.postings.elementAt(i1Doc).docid < list2.postings.elementAt(i2Doc).docid)) {
        i1Doc++;
      }

      
      //if same DocId then check position of these terms in the document
      /****************************************************/
      if ((i1Doc < list1.df)
          && (list1.postings.elementAt(i1Doc).docid == list2.postings.elementAt(i2Doc).docid)) {
        int p1=0,p2=0;
        
        Vector<Integer> pos1 =  list1.postings.elementAt(i1Doc).positions;
        Vector<Integer> pos2 =  list2.postings.elementAt(i2Doc).positions;
        DocPosting doc = new DocPosting(list1.postings.elementAt(i1Doc).docid);
        
       
        while(p1<pos1.size() && p2<pos2.size())
        {
          if(pos2.elementAt(p2) < pos1.elementAt(p1)) //ex pos1 = 35 pos2 =17 n = 1
          {
            p2++;
          }
          else if(pos2.elementAt(p2) - pos1.elementAt(p1) <= n) //ex pos1 = 35 pos2 = 36 n=1
          {
            
            doc.tf++;
            doc.positions.add(p2);
            p1++;
            p2++;
          }
          else //ex pos1 = 45 pos2 = 49 n=1
          {
            p1++;
          }
          
        }
        
        if(doc.tf>0)
        {
          result.invertedList.postings.add(doc);
          df++;
          ctf += doc.tf;
        }
         
        i1Doc++;
        i2Doc++;
        /* Merge the i1 and i2 postings for this document */
        //this.postings.elementAt(i1Doc).insert(i2.postings.elementAt(i2Doc));
        //this.ctf += i2.postings.elementAt(i2Doc).tf;
      } 
      /****************************************************/
      
      else {
        i2Doc++;        
      }

    }

    result.invertedList.df = df;
    result.invertedList.ctf = ctf;
    
    return result;
  }
}
