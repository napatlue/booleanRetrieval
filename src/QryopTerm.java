/*
 *  Copyright (c) 2013, Carnegie Mellon University.  All Rights Reserved.
 */

import java.io.IOException;

public class QryopTerm extends Qryop {

  private String term;
  private String field;

  /* Constructors */
  public QryopTerm(String t) {
    this.term = t;
    this.field = "body"; /* Default field if none is specified */
  }

  /* Constructor */
  public QryopTerm(String t, String f) {
    this.term = t;
    this.field = f;
  }

  /**
   * Evaluate the query operator.
   */
  public QryResult evaluate() throws IOException {
    QryResult result = new QryResult();
    result.invertedList = new InvList(this.term, this.field);
    return result;
  }
}
