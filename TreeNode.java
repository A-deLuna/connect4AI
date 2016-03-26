package bot;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class TreeNode {
  static Random r = new Random();
  static double epsilon = 1e-6;
  private Field mField;
  private int mId;

  TreeNode[] children;
  public double nVisits, totValue;
  public int mCol;

  TreeNode() {
  }

  TreeNode(int col, int id) {
    this.mCol = col;
    this.mId = id;
  }

  void setField(Field f) {
    this.mField = f;
  }

  void setId (int id) {
    this.mId = id == 1 ? 2 : 1;
  }

  public int mostVisitedCol() {
    TreeNode mostVisited = children[0];

    for(TreeNode n : children) {
      if(mostVisited.nVisits < n.nVisits) {
        mostVisited = n;
      }
    }
    return mostVisited.mCol;
  }

  public void selectAction() {
    List<TreeNode> visited = new LinkedList<TreeNode>();
    TreeNode cur = this;
    // visited.add(this);
    Field fieldClone = mField.clone();
    int id = this.mId == 1? 2: 1;
    while (!cur.isLeaf()) {
      if(cur.select() == null) {
          cur.select();
      }
      cur = cur.select();
      
      fieldClone.addDisc(cur.mCol, id);
     
      visited.add(cur);
      id = id == 1 ? 2: 1;
    }
    fieldClone.addDisc(cur.mCol, id);
    cur.expand(fieldClone);
    TreeNode newNode = cur.select();
    // check if this is a terminal state
    if(newNode != null) {
      visited.add(newNode);
    }
    
    int value = rollOut(visited);
    
  
    for (TreeNode node : visited) {
      // would need extra logic for n-player game 
      node.updateStats(value);
    }
    nVisits++;
  }

  public void expand(Field field) {
    if(!field.continueGame()) {
      return;
    }
    List<Integer> validMoves = field.validMoves();
    children = new TreeNode[validMoves.size()];
    for (int i=0; i<validMoves.size(); i++) {
      int id = this.mId == 1 ? 2 : 1;
      children[i] = new TreeNode(validMoves.get(i), id);
    }
  }

  private TreeNode select() {
    TreeNode selected = null;
    double uctValue = 0.0d;
    double bestValue = -1000000000d;
    if(children == null) return null;
    for (TreeNode c : children) {
      uctValue = c.totValue / (c.nVisits + epsilon) +
             Math.sqrt(Math.log(nVisits+1) / (c.nVisits + epsilon)) +
               r.nextDouble() * epsilon;
      // small random number to break ties randomly in unexpanded nodes
      if (uctValue > bestValue) {
        selected = c;
        bestValue = uctValue;
      }
    }
    return selected;
  }

  public boolean isLeaf() {
    return children == null;
  }
  // 0 tie, 1 
  public int rollOut(List<TreeNode> visited) {
    Field fieldClone = mField.clone();
    int id = mId == 1 ? 2: 1;
    for (TreeNode node : visited) {
      fieldClone.addDisc(node.mCol, id);
      if(!fieldClone.continueGame()) {
        if(fieldClone.isFull()) {
          return 0;
        }
        return id;
      }
      id = (id == 1) ? 2 : 1;
    }
    while(fieldClone.continueGame()) {
      List<Integer> moves = fieldClone.validMoves();
      int i = r.nextInt(moves.size());
      fieldClone.addDisc(moves.get(i), id);
      id = (id == 1) ? 2 : 1;
    }

    if(fieldClone.isFull()) {
      return 0;
    }
    id = (id == 1) ? 2 : 1;
    return id;
    
  }

  public void updateStats(int won) {
    double value;
    if(won == 0) {
        value = 0.0d;
    }
    else {
        if(this.mId == won) {
            value = 1.0d;
        }
        else {
            value = -1.0d;
        }
    }
    nVisits++;
    totValue += value;
  }

  public int arity() {
    return children == null ? 0 : children.length;
  }
}
