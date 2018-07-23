package Sat;

import java.util.*;

public class Clause extends HashSet<Literal> {

  private boolean satisfied = false;

  public Clause(Collection<? extends Literal> collection) {
    super(collection);
  }

  public boolean isSatisfied() {
    return satisfied;
  }

  public void setSatisfied(boolean satisfied) {
    this.satisfied = satisfied;
  }

  public List<Literal> unknownLiterals(Map<Atom, Boolean> knownValues) {
    List<Literal> result = new ArrayList<>();

    for (Literal literal : this)
      if (!knownValues.containsKey(literal.getAtom()))
        result.add(literal);

    return result;
  }

  public boolean checkIfSat(Map<Atom, Boolean> knownValues) {
    for (Literal literal : this) {
      if (!knownValues.containsKey(literal.getAtom()))
        continue;
      if (literal.isNegated()) {
        if (knownValues.get(literal.getAtom()) == false) {
          return true;
        }
      } else {
        if (knownValues.get(literal.getAtom()) == true) {
          return true;
        }
      }
    }
    return false;
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
}
