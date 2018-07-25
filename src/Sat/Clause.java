package Sat;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A {@link Set} of {@link Literal}s OR'd together <Br>
 * (¬A or B or C or ¬D) etc...
 */
public class Clause extends HashSet<Literal> {

  // clause is satisfied when one of it's literals evaluates to true
  private boolean satisfied = false;

  public Clause(Collection<? extends Literal> collection) {
    super(collection);
  }

  /**
   * returns a list of all literals in this clause whose values
   * are not present in the passed map
   *
   * @param atomValues A map of atom->value pairs
   * @return A list of all literals in this clause whose values
   * are not present in the passed map
   */
  public List<Literal> unknownLiterals(Map<Atom, Boolean> atomValues) {
    return this.stream()
        .filter(literal -> !atomValues.containsKey(literal.getAtom()))
        .collect(Collectors.toList());
  }

  /**
   * Determines if this clause is satisfied according to the passed atom values.
   * This will modify the satisfied field to equal the return value.
   *
   * @param atomValues A map of atom->value pairs
   * @return true if this clause is satisfied, false otherwise
   */
  public boolean isSatisfied(Map<Atom, Boolean> atomValues) {
    return this.satisfied = this.stream()
        .anyMatch(literal ->
            atomValues.getOrDefault(literal.getAtom(), literal.isNegated()) == !literal.isNegated());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (this.getClass() != obj.getClass())
      return false;

    Clause other = (Clause) obj;
    return this.containsAll(other)
        && other.containsAll(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.toArray());
  }

  public boolean isSatisfied() {
    return satisfied;
  }

  public void setSatisfied(boolean satisfied) {
    this.satisfied = satisfied;
  }
}
