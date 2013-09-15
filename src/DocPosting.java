import java.util.Vector;



 /**
   * Utility class that makes it easier to deal with postings.
   */
  public class DocPosting {

    public int docid = 0;
    public int tf = 0;
    public Vector<Integer> positions = new Vector<Integer>();

    /**
     * Constructor.
     */
    public DocPosting(int d, int... locations) {
      this.docid = d;
      this.tf = locations.length;
      for (int i = 0; i < locations.length; i++)
        this.positions.add(locations[i]);
    }

    /**
     * Inserts the postings from dp2 into this (dp1) docPosting. Maintains a sorted order.
     */
    public void insert(DocPosting dp2) {

      int dp1Pos = 0; /* Offset to the current position in dp1 */
      int dp2Pos = 0; /* Offset to the current position in dp2 */

      // Each pass of the loop inserts one position from dp2 into dp1.
      while (dp2Pos < dp2.tf) {

        // Skip positions in dp1 that are less than the current position in dp2.
        while ((dp1Pos < this.tf)
            && (this.positions.elementAt(dp1Pos) < dp2.positions.elementAt(dp2Pos))) {
          dp1Pos++;
        }

        // dp1Pos points to where a dp2 position can be inserted (possibly at the end of the list).
        // If it is not a duplicate, insert it.
        if ((dp1Pos == this.tf)
            || (this.positions.elementAt(dp1Pos) > dp2.positions.elementAt(dp2Pos))) {

          this.positions.add(dp1Pos, dp2.positions.elementAt(dp2Pos));
          this.tf++;
        }

        dp1Pos++;
        dp2Pos++;
      }
    }
  }