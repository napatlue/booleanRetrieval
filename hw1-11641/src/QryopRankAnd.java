

import java.io.IOException;

public class QryopRankAnd extends Qryop {

  /**
   * It is convenient for the constructor to accept a variable number of arguments. Thus new
   * qryopAnd (arg1, arg2, arg3, ...).
   */
  public QryopRankAnd(Qryop... q) {
    for (int i = 0; i < q.length; i++)
      this.args.add(q[i]);
  }

  /**
   * Evaluate the query operator.
   */
  public QryResult evaluate() throws IOException {

    // Seed the result list by evaluating the first query argument. The result could be docScores or
    // invList, depending on the query operator. Wrap a SCORE query operator around it to force it
    // to be a docScores list. There are more efficient ways to do this. This approach is just easy
    // to see and understand.
    Qryop impliedQryOp = new QryopScore(args.get(0),true);
    QryResult result = impliedQryOp.evaluate();

    // Each pass of the loop evaluates one query argument.
    for (int i = 1; i < args.size(); i++) {

      impliedQryOp = new QryopScore(args.get(i),true);
      QryResult iResult = impliedQryOp.evaluate();

      // Use the results of the i'th argument to incrementally compute the query operator.
      // Intersection-style query operators iterate over the incremental results, not the results of
      // the i'th query argument.
      int rDoc = 0; /* Index of a document in result. */
      int iDoc = 0; /* Index of a document in iResult. */

      while (rDoc < result.docScores.scores.size()) {

        // DIFFERENT RETRIEVAL MODELS IMPLEMENT THIS DIFFERENTLY.
        // Unranked Boolean AND. Remove from the incremental result any documents that weren't 
        // returned by the i'th query argument.

        // Ignore documents matched only by the i'th query arg.
        while ((iDoc < iResult.docScores.scores.size())
            && (result.docScores.getDocid(rDoc) > iResult.docScores.getDocid(iDoc))) {
          iDoc++;
        }

        // If the rDoc document appears in both lists, keep it, otherwise discard it.
        if ((iDoc < iResult.docScores.scores.size())
            && (result.docScores.getDocid(rDoc) == iResult.docScores.getDocid(iDoc))) {
          float rScore = result.docScores.getDocidScore(rDoc); 
          float iScore = iResult.docScores.getDocidScore(iDoc);
          result.docScores.setDocidScore(rDoc, Math.min(rScore,iScore));
          rDoc++;
          iDoc++;
        } else {
          result.docScores.scores.remove(rDoc);
        }
      }
    }

    return result;
  }
}
