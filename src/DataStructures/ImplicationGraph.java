package DataStructures;

import Sat.Atom;
import Sat.Clause;
import Sat.Literal;

import java.util.*;

public class ImplicationGraph {

  public final Map<Atom, List<Atom>> implies = new HashMap<>();

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
