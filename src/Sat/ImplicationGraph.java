package Sat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A graph data structure to keep track of which set of atoms
 * implied the value of other atoms via unit propagation.
 */
public class ImplicationGraph {

  public final Map<Atom, List<Atom>> implies = new HashMap<>();

  /**
   * Stores the implication that all but 1 atoms in a clause implies
   * the final atom in that clause via unit propagation
   *
   * @param clause      The clause which unit propagation is taking place
   * @param impliedAtom The atom which is being inferred via unit propagation
   */
  public void implies(Clause clause, Atom impliedAtom) {
    for (Literal literal : clause) {
      Atom atom = literal.getAtom();
      if (atom.equals(impliedAtom))
        continue;

      implies.putIfAbsent(atom, new ArrayList<>());
      implies.get(atom).add(impliedAtom);
    }
  }
}
