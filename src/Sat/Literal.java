package Sat;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An {@link Atom} which may be negated. <br>
 * a, ¬a, b, ¬b...
 */
public class Literal {

  // map (K: Objects.hash(literal) , V:literal)
  private static Map<Integer, Literal> allLiterals = new HashMap<>();

  private Atom atom;
  private boolean negated;

  /**
   * @param atom    the Atom this Literal refers to
   * @param negated true if this Literal is negated, e.g. ¬A
   */
  private Literal(Atom atom, boolean negated) {
    this.atom = atom;
    this.negated = negated;

    allLiterals.put(Objects.hash(atom, negated), this);
  }

  /**
   * Creates a non-negated Literal
   *
   * @param name the name of the atom this literal refers to
   * @return The created Literal
   */
  public static Literal createLiteral(String name) {
    return createLiteral(name, false);
  }

  /**
   * Creates a new Literal
   *
   * @param name    the name of the atom this literal refers to
   * @param negated true for a negated literal. e.g. ¬A
   * @return The created Literal
   */
  public static Literal createLiteral(String name, boolean negated) {
    Atom atom = Atom.createAtom(name);
    Literal literal = allLiterals.get(Objects.hash(atom, negated));
    return literal == null
        ? new Literal(atom, negated)
        : literal;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (this.getClass() != obj.getClass())
      return false;

    Literal other = (Literal) obj;
    return other.negated == this.negated
        && other.atom.equals(this.atom);
  }

  @Override
  public int hashCode() {
    return Objects.hash(atom, negated);
  }

  @Override
  public String toString() {
    return isNegated() ? "¬" + atom.toString() : atom.toString();
  }

  public Atom getAtom() {
    return atom;
  }

  public boolean isNegated() {
    return negated;
  }
}
