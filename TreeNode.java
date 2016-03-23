package bot;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class TreeNode {
  static Random r = new Random();
  private int nActions;
  static double epsilon = 1e-6;
  private Field mField;
  private int mId;

  TreeNode[] children;
  public double nVisits, totValue;
  public int mCol;

  TreeNode(int actions) {
    this.nActions = actions;
  }
  TreeNode(int actions, int col, int id) {
    this.nActions = actions;
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
    while (!cur.isLeaf()) {
      cur = cur.select();
      visited.add(cur);
    }
    cur.expand();
    TreeNode newNode = cur.select();
    visited.add(newNode);
    int value = rollOut(newNode, visited);
    for (TreeNode node : visited) {
      // would need extra logic for n-player game
      node.updateStats(value);
    }
    nVisits++;
  }

  public void expand() {
    children = new TreeNode[nActions];
    for (int i=0; i<nActions; i++) {
      int id = this.mId == 1 ? 2 : 1;
      children[i] = new TreeNode(nActions, i, id);
    }
  }

  private TreeNode select() {
    TreeNode selected = null;
    TreeNode help = this;
    double uctValue = 0.0d;
    double bestValue = -1000000000d;
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
  public int rollOut(TreeNode tn, List<TreeNode> visited) {
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
