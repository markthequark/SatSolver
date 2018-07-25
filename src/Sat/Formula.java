package Sat;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link List} of {@link Clause}(s) AND'd together <Br>
 * (¬A or B) and (A or B) and (A or ¬B) etc...
 */
public class Formula extends ArrayList<Clause> {

  /**
   * add a Stringly-typed clause to this formula <br>
   * e.g. formula.addClause("A", "¬B", "C");
   *
   * @param literals A String varargs representing Literals. <Br>
   *                 negative Literals should begin with ¬ e.g. "¬A", "¬x1"
   */
  public void addClause(String... literals) {
    List<Literal> literalList = new ArrayList<>();
    for (String id : literals) {
      literalList.add(id.charAt(0) == '¬'
          ? Literal.createLiteral(id.substring(1), true)
          : Literal.createLiteral(id));
    }
    Clause clause = new Clause(literalList);
    if (this.contains(clause))
      return;

    this.add(clause);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("(and");
    for (Clause clause : this) {
      sb.append("     (or ");
      for (Literal literal : clause) {
        sb.append(literal.toString()).append(" ");
      }
      sb.deleteCharAt(sb.length() - 1);
      sb.append(")\n");
    }
    sb.deleteCharAt(sb.length() - 1);
    sb.append(")\n");

    sb.replace(5, 9, "");
    return sb.toString();
  }
}
