import java.io.IOException;
import java.util.Comparator;


public class DocScoreComparator implements Comparator<ScoreList.ScoreListEntry> {
    public int compare(ScoreList.ScoreListEntry o1, ScoreList.ScoreListEntry o2) {
      if(o1.getScore() < o2.getScore())
      {
        return 1;
      }
      else if(o1.getScore() > o2.getScore())
      {
        return -1;
      }
      else
      {
        String doc1 = "";
        try {
          doc1 = QryEval.getExternalDocid(o1.getDocId());
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        String doc2 = "";
        try {
          doc2 = QryEval.getExternalDocid(o2.getDocId());
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        if(doc1.compareTo(doc2) > 0)
        {
          return 1;
        }
        else
        {
          return -1;
        }
      }
      /*
      o1.getScore()  
      int value1 = o1.getScore().);
        if (value1 == 0) {
            int value2 = o1.faculty.compareTo(o2.faculty);
            if (value2 == 0) {
                return o1.building.compareTo(o2.building);
            } else {
                return value2;
        }
        return value1;
       */
    }
}