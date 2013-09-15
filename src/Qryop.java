/*
 *  Copyright (c) 2013, Carnegie Mellon University.  All Rights Reserved.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Qryop {

  protected List<Qryop> args = new ArrayList<Qryop>();
  
  /**
   * Evaluates the query operator, including any child operators and returns the result.
   * @return {@link QryResult} object
   * @throws IOException
   */
  public abstract QryResult evaluate() throws IOException;
  
  //Constructor with 0 argument
  public Qryop(){}
}
