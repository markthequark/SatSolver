package Sat;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * Java implementation of the DPLL algorithm for solving the boolean satisfiability problem.
 * https://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SatSolver {
  
  /**
   * Example usage of algorithm
   */
  public static void main(String[] args) {

    /* Create a Formula representing the boolean expression:
    
       (A V B V ¬C) ^ (¬A V ¬B V C) ^ (¬B V C V D)
       
       alternatively written as:
       
       AND(OR(A, B, NOT C),
           OR(NOT A, NOT B, C),
           OR(NOT B, C, D))
    */
    Formula f1 = new Formula();
    f1.addClause("A", "B", "¬C");
    f1.addClause("¬A", "¬B", "C");
    f1.addClause("¬B", "C", "D");

    boolean result = SatSolver.solve(f1);
    System.out.println("Satisfiability: " + result);
  }

  private static boolean solve(Formula formula) {
    formula.sort(Comparator.comparingInt(Clause::size));
    System.out.println("solving\n" + formula.toString());

    // tree of atom-value guesses
    BinaryTree guessTree = new BinaryTree();
    // graph of atom-value pairs determined from known atom-value pairs
    ImplicationGraph implicationGraph = new ImplicationGraph();
    // current accepted atom-value pairs
    Map<Atom, Boolean> atomValues = new HashMap<>();

    main:
    while (true) {
      if (guessTree.root.isUnsat())
        return false;

      // loop allows multiple atoms to be determined via unit propagation
      unitPropagation:
      while (true) {
        for (Clause clause : formula) {
          clause.update(atomValues);
          if (clause.isSatisfied())
            continue;

          List<Literal> unknownLiterals = clause.unknownLiterals(atomValues);
          if (unknownLiterals.size() == 0) {
            // if here, clause evaluates to false
            // undoes previous atom-value guess, and makes a new one
            System.out.println("reversing guess of\t" + guessTree.getAtom() + " = " + guessTree.getValue());
            guessTree = guessTree.reverseGuess(atomValues, implicationGraph);
            continue main;
          }
          if (unknownLiterals.size() == 1) {
            // if here, can determine last atom in clause via unit propagation
            Literal unknownLiteral = unknownLiterals.get(0);
            implicationGraph.implies(clause, unknownLiteral.getAtom());
            
            atomValues.put(unknownLiteral.getAtom(), !unknownLiteral.isNegated());
            System.out.println("unit propagation:\t" + unknownLiteral.getAtom().id + " = " + !unknownLiteral.isNegated());

//            perhaps a more readable variation of the above 2 lines is:
//             if (unknownLiteral.isNegated()) {
//               atomValues.put(unknownLiteral.getAtom(), false);
//               System.out.println("unit propagation:\t" + unknownLiteral.getAtom().id + " = false");
//             } else {
//               atomValues.put(unknownLiteral.getAtom(), true);
//               System.out.println("unit propagation:\t" + unknownLiteral.getAtom().id + " = true");
//             }
            
            continue unitPropagation;
          }
        }
        break;
      }

      // check for solved state
      if (formula.stream().allMatch(Clause::isSatisfied))
        return true;

      // cannot continue with unit propagation, therefore, pick & guess a variable
      for (Clause clause : formula) {
        if (clause.isSatisfied())
          continue;
        Atom unknownAtom = clause.unknownLiterals(atomValues).get(0).getAtom();
        guessTree = guessTree.addChild(unknownAtom, true);
        atomValues.put(unknownAtom, true);
        System.out.println("guessing:\t\t\t" + unknownAtom + " = true");
        break;
      }
    }
  }
}
