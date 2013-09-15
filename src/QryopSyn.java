/*
 *  Copyright (c) 2013, Carnegie Mellon University.  All Rights Reserved.
 */

import java.io.IOException;

public class QryopSyn extends Qryop {

  /**
   * It is convenient for the constructor to accept a variable number of arguments. Thus new
   * qryopSyn (arg1, arg2, arg3, ...).
   */
  public QryopSyn(Qryop... q) {
    for (int i = 0; i < q.length; i++)
      this.args.add(q[i]);
  }

  /**
   * Evaluate the query operator.
   */
  public QryResult evaluate() throws IOException {

    // Seed the result list by evaluating the first query argument.
    QryResult result = args.get(0).evaluate();

    // Each pass of the loop evaluates one query argument.
    for (int i = 1; i < args.size(); i++) {
      result.invertedList.insert(args.get(i).evaluate().invertedList);
    }

    return result;
  }
}
