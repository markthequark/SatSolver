package Sat;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.LinkedList;
import java.util.Map;

/**
 * A tree data structure to keep track of the atom-value guesses which have been made
 * <p>
 * child nodes with value false are assigned to the left of it's parent <br>
 * child nodes with value true are assigned to the right of it's parent <br>
 * <p>
 * nodes hold an atom, and it's associated value guess <br>
 * both child nodes of a single parent must have the same atom, this is enforced
 */
public class BinaryTree {

  /**
   * A pointer to the root of this tree
   */
  @NotNull
  public final BinaryTree root;

  @Nullable
  private final BinaryTree parent;
  private BinaryTree left;
  private BinaryTree right;

  private Atom atom;
  private boolean value;

  // when true, all children branching from this node produces an unsatisfiable formula
  private boolean unsat = false;

  /**
   * Creates a new binary tree. <Br>
   * The returned BinaryTree points to it's root.
   */
  public BinaryTree() {
    this.root = this;
    this.parent = null;
  }

  private BinaryTree(BinaryTree parent, Atom atom, boolean value) {
    this.root = parent.root;
    this.parent = parent;
    this.atom = atom;
    this.value = value;
  }

  /**
   * Creates a new child node on this node and returns a pointer to that child <br>
   *
   * @param atom  The atom contained in this new node
   * @param value The value of the atom contained in this new node
   * @return a A pointer to the newly created child node
   * @throws InvalidStateException if this node already had a child with a different atom.
   *                               (both child nodes of a parent node must have the same atom)
   */
  public BinaryTree addChild(Atom atom, boolean value) {
    BinaryTree result;
    if (value)
      result = right == null ? right = new BinaryTree(this, atom, value) : right;
    else
      result = left == null ? left = new BinaryTree(this, atom, value) : left;

    // check atoms on equal levels of tree are equal
    if (right != null && left != null
        && !right.atom.equals(left.atom))
      throw new InvalidStateException("atoms on equal level of binary tree must be equal");

    return result;
  }

  /**
   * Sets the unsat field of this node to true <br>
   * Attempts to reverse the guess of the parent, and returns a pointer to the tree
   * at the new child, with the new guess. <br>
   * If both children of the parent node leads to an unsat node, then attempts to
   * reverse the guess of the parent of the parent, recursively.
   * <p>
   * If reverseGuess is called on the root of a tree, the root node will be returned,
   * after setting the root node unsat field to true.
   *
   * @param atomValues       The map of atom-value pairs. This will be modified to not
   *                         conflict with the reversed guess
   * @param implicationGraph The graph of implied atom-value pairs, This will be
   *                         modified to not conflict with the reversed guess
   * @return A pointer to the tree at the new guess. Or a pointer to the root node if
   * all guesses lead to an unsat node.
   */
  public BinaryTree reverseGuess(Map<Atom, Boolean> atomValues, ImplicationGraph implicationGraph) {
    this.unsat = true;
    if (this == this.root)
      return this;

    // remove all data gained from current guess
    LinkedList<Atom> impliedAtoms = new LinkedList<>();
    impliedAtoms.add(atom);
    Atom atom = this.atom;
    while (true) {
      while (implicationGraph.implies.containsKey(atom)
          && implicationGraph.implies.get(atom).size() > 0) {
        atom = implicationGraph.implies.get(atom).get(0);
        impliedAtoms.add(atom);
      }
      // remove from map
      atomValues.remove(atom);
      // remove from local list
      impliedAtoms.pollLast();
      // remove from graph
      if (atom != this.atom)
        implicationGraph.implies.get(impliedAtoms.peekLast()).remove(atom);
      else {
        implicationGraph.implies.remove(atom);
        break;
      }
      atom = impliedAtoms.peekLast();
    }

    // guess other value
    if ((value && parent.left == null) || (!value && parent.right == null)) {
      atomValues.put(this.atom, !value);
      return parent.addChild(this.atom, !value);
    }
    // else if both values determined to be unsat, reverse guess of parent
    return parent.reverseGuess(atomValues, implicationGraph);
  }

  public boolean isUnsat() {
    return unsat;
  }

  public Atom getAtom() {
    return atom;
  }

  public boolean getValue() {
    return value;
  }
}
