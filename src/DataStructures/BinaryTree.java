package DataStructures;

import Sat.Atom;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.LinkedList;
import java.util.Map;

/**
 * leaf nodes with value false are assigned to left.
 * leaf nodes with value true are assigned to right.
 *
 * nodes hold an atom, and it's associated value guess
 * both child nodes of a single parent must have the same atom, this is enforced
 */
public class BinaryTree {

  @NotNull
  public final BinaryTree root;

  @Nullable
  private final BinaryTree parent;
  private  BinaryTree left;
  private BinaryTree right;

  private Atom atom;
  private boolean value;

  // when true, all children branching from this node produces an unsatisfiable formula
  private boolean unsat = false;

  public BinaryTree() {
    this.root = this;
    this.parent = null;
    this.atom = atom;
  }

  public BinaryTree(BinaryTree parent, Atom atom, boolean value) {
    this.root = parent.root;
    this.parent = parent;
    this.atom = atom;
    this.value = value;
  }

  public BinaryTree addChild(Atom atom, boolean value) {
    BinaryTree result;
    if(value)
      result = right == null ? right = new BinaryTree(this, atom, value) : right;
    else
      result = left == null ? left = new BinaryTree(this, atom, value) : left;

    // check atoms on equal levels of tree are equal
    if (right != null && left != null
        && !right.atom.equals(left.atom))
      throw new InvalidStateException("atoms on equal level of binary tree must be equal");

    return result;
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

  public BinaryTree reverseGuess(Map<Atom, Boolean> knownValues, ImplicationGraph implicationGraph) {
    this.unsat = true;
    if (this == this.root)
      return this;

    System.out.println(knownValues.toString());
    System.out.println(implicationGraph.implies.toString());
    System.out.println(atom);
    System.out.println(value);
    System.exit(1);

    // remove all data gained from current guess
    LinkedList<Atom> impliedAtoms = new LinkedList<>();
    impliedAtoms.add(atom);
    Atom atom = this.atom;
    while (true) {
      while (implicationGraph.implies.containsKey(atom)) {
        atom = implicationGraph.implies.get(atom).get(0);
        impliedAtoms.add(atom);
      }
      // remove from map
      knownValues.remove(atom);
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
      knownValues.put(this.atom, !value);
      return parent.addChild(this.atom, !value);
    }
    // else if both values determined to be unsat, reverse guess of parent
    return parent.reverseGuess(knownValues, implicationGraph);
  }
}
