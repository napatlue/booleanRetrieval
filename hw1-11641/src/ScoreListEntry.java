import java.io.IOException;



public class ScoreListEntry {
    private int docid;
    private float score;
    
    
    public ScoreListEntry(int docid, float score) {
      this.docid = docid;
      this.score = score;
    }
    
    public void setScore(float score){
      this.score = score;
    }
    
    public float getScore(){
      return this.score;
    }
    public int getDocId(){
      return this.docid;
    }
    
    public int compareTo(ScoreListEntry o2)
    {
      if(this.getScore() < o2.getScore())
      {
        return -1;
      }
      else if(this.getScore() > o2.getScore())
      {
        return 1;
      }
      else
      {
//        String doc1 = "";
//        try {
//          doc1 = QryEval.getExternalDocid(this.getDocId());
//        } catch (IOException e) {
//          // TODO Auto-generated catch block
//          e.printStackTrace();
//        }
//        String doc2 = "";
//        try {
//          doc2 = QryEval.getExternalDocid(o2.getDocId());
//        } catch (IOException e) {
//          // TODO Auto-generated catch block
//          e.printStackTrace();
//        }
        //if(doc1.compareTo(doc2) > 0)
        if(this.getDocid() > o2.getDocid())
        {
          return -1;
        }
        else
        {
          return 1;
        }
      }
      
    }

    public int getDocid() {
      return docid;
    }

    public void setDocid(int docid) {
      this.docid = docid;
    }
    
  }