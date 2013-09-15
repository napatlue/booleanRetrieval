/*
 *  Copyright (c) 2013, Carnegie Mellon University.  All Rights Reserved.
 */


import java.util.ArrayList;
import java.util.List;

public class ScoreList {

  /**
   * A little utilty class to create a <docid, score> object.
   */
  

  List<ScoreListEntry> scores = new ArrayList<ScoreListEntry>();

  /**
   * Append a document score to a score list.
   */
  public void add(int docid, float score) {
    scores.add(new ScoreListEntry(docid, score));
  }

  public int getDocid(int n) {
    return this.scores.get(n).getDocid();
  }

  public float getDocidScore(int n) {
    return this.scores.get(n).getScore();
  }

  public void setDocidScore(int n,float score) {
    this.scores.get(n).setScore(score);
  }
  public void setScoreEntry(int n,ScoreListEntry entry) {
    this.scores.set(n, entry);
  }
}
