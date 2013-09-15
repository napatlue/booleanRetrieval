/**
 * 
 */

import java.io.IOException;
/**
 * @author napatluevisadpaibul
 *
 */
import java.util.ArrayList;




public class TreeNode {

  public enum Type {
    TERM, AND, OR, NEAR, NULL
  };

  private Type type;

  private String term;

  private int n; // for Near Operation

  private ArrayList<TreeNode> children;

  /**
   * 
   */
  public TreeNode() {
    // TODO Auto-generated constructor stub
    this.term = "";
    this.n = 0;
    this.type = Type.NULL;
  }

  public void AddChild(TreeNode node) {
    this.children.add(node);
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public String getTerm() {
    return term;
  }

  public void setTerm(String term) {
    this.term = term;
  }

  public int getN() {
    return n;
  }

  public void setN(int n) {
    this.n = n;
  }

  public ArrayList<TreeNode> getChildren() {
    return children;
  }

  public void setChildren(ArrayList<TreeNode> children) {
    this.children = children;
  }

  public ArrayList<TreeNode> MakeChildren(String line){
    // TODO Auto-generated method stub
    int startIndex = 0;
    int operIndex = 0;
    line = line.trim();
    // test = "#AND (aparagus #OR (broccoli cauliflower) #SYN(peapods peas))";
    String oper;
    String termStr;
    String[] termList = {""};
    ArrayList<TreeNode> children = new ArrayList<TreeNode>();

    while((operIndex=line.indexOf("#", startIndex))>=0) //loop until there are no operation left
    {
      
      // These lines of code add term ie aparagus broccoli cautiflower to children
      //-----------------------------------------------------------------------------
      termStr = line.substring(startIndex,operIndex);
      
      try {
         termList = QryEval.tokenizeQuery(termStr);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      for(int i=0;i< termList.length;i++)
      {
        TreeNode node = new TreeNode();
        node.setTerm(termList[i]);
        node.setType(Type.TERM);
        children.add(node);
      }
      //-----------------------------------------------------------------------------
      
      
      //these line create new node which represent operator ie AND OR NEAR, then make its children(operand), construct subtree
      //-----------------------------------------------------------------------------
      startIndex = operIndex + 1;
      oper = line.substring(startIndex, startIndex + 2);
      TreeNode node = new TreeNode();
      if (oper.equalsIgnoreCase("OR")) {
        node.setType(TreeNode.Type.OR);
        startIndex += 3;
      } else {
        oper = line.substring(startIndex, startIndex + 3);
        if (oper.equalsIgnoreCase("AND")) {
          node.setType(TreeNode.Type.AND);
          startIndex += 4;

        } else {
          oper = line.substring(startIndex, startIndex + 4);
          if (oper.equalsIgnoreCase("NEAR")) {
            startIndex += 5;
            int parIndex = line.indexOf("(", startIndex); // index of ( next to NEAR
            String num = line.substring(startIndex, parIndex);
            int n = Integer.parseInt(num.trim());
            node.setType(TreeNode.Type.NEAR);
            node.setN(n);
            startIndex = parIndex + 1;

          }
        }
      }
      
      //check parenthesis to get scope of operation
      int cnt = 0;
      int endIndex = startIndex-1;
      for(;endIndex < line.length();endIndex++){
        if(line.charAt(endIndex) == ' ')
        {
          continue;
        }
        if(line.charAt(endIndex) == '(')
        {
          cnt++;
        }
        else if(line.charAt(endIndex) == ')')
        {
          cnt--;
        }
        if(cnt == 0)
        {
          break;
        }
        
      }
      
      String childStr = line.substring(startIndex, endIndex);
      node.setChildren(MakeChildren(childStr));
      children.add(node);
      startIndex = endIndex+1;
      //-----------------------------------------------------------------------------
    }
    
    //Add all leftover terms  that come after operation(if any)
    //-----------------------------------------------------------------------------
    termStr = line.substring(startIndex);
    
    try {
       termList = QryEval.tokenizeQuery(termStr);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    for(int i=0;i< termList.length;i++)
    {
      TreeNode node = new TreeNode();
      node.setTerm(termList[i]);
      node.setType(Type.TERM);
      children.add(node);
    }
    //-----------------------------------------------------------------------------

    return children;
  }

  public Qryop buildOperation(QryEval.Mode mode) {
    Qryop op = new QryopOr();
    if(this.getType() == Type.NULL)
    {
      return this.getChildren().get(0).buildOperation(mode);
    }
    
    
    if(this.getType() == Type.AND)
    {
      ArrayList<TreeNode> children = this.getChildren();
      if(mode == QryEval.Mode.UNRANKED_BOOLEAN)
      {
        op = new QryopAnd();
      }
      else
      {
        op = new QryopRankAnd();
      }
      for(int i = 0; i<children.size();i++)
      {
        op.args.add(children.get(i).buildOperation(mode)); 
      }
      return op;
    }
    else if(this.getType() == Type.OR)
    {
      ArrayList<TreeNode> children = this.getChildren();
      if(mode == QryEval.Mode.UNRANKED_BOOLEAN)
      {
        op = new QryopOr();
      }
      else
      {
        op = new QryopRankOr();
      }
      for(int i = 0; i<children.size();i++)
      {
        op.args.add(children.get(i).buildOperation(mode)); 
      }
      return op;
    }
    else if(this.getType() == Type.NEAR)
    {
      ArrayList<TreeNode> children = this.getChildren();
      op = new QryopNear(this.getN());

      //near operator should have 2 children
      op.args.add(children.get(0).buildOperation(mode));
      op.args.add(children.get(1).buildOperation(mode)); 
      
      return op;
    }
    else if(this.getType() == Type.TERM)
    {
      String word = "";
      String field = "body";
      String term = this.getTerm();
      int ind;
      if((ind=term.indexOf(".")) > 0)
      {
        word = term.substring(0,ind);
        field = term.substring(ind+1);
      }
      else
      {
        word = term;
      }
        
      return new QryopTerm(word,field);
    }
    
    return op;
  }

  public QryResult eval(QryEval.Mode mode) {
    // TODO Auto-generated method stub
    try {
      Qryop op = this.buildOperation(mode);
      QryResult result = op.evaluate();
      return result;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  

}
