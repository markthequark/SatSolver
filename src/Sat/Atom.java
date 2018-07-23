package Sat;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * A propositional variable. <br>
 * a, b, c...
 */
public class Atom {

  // TreeMap of (K:id, V:atom) keys ordered by their hashcode
  private static Map<String, Atom> allAtoms = new TreeMap<>();

  public final String id;

  private Atom(String name) {
    this.id = name;

    Atom.allAtoms.put(name, this);
  }

  public static Atom createAtom(String name) {
    Atom atom = allAtoms.get(name);
    return atom == null ? new Atom(name) : atom;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (this.getClass() != obj.getClass())
      return false;

    Atom other = (Atom) obj;
    return other.id.equals(this.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return id;
  }
}
