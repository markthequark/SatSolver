package Sat;

import java.util.*;

public class SatSolver {
  public static void main(String[] args) {

    Formula f1 = new Formula();
    f1.addClause("A", "B", "¬C");
    f1.addClause("C", "D", "¬B");
    f1.addClause("¬A", "E", "F");
    f1.addClause("B", "¬C", "¬F");
    f1.addClause("¬C", "¬E", "¬F");
    f1.addClause("A", "¬B", "E");
    f1.addClause("¬A", "¬B", "C");

    boolean result = SatSolver.solve(f1);
    System.out.println("Satisfiability: " + result);
  }

  private static boolean solve(Formula formula) {
    Collections.sort(formula, Comparator.comparingInt(Clause::size));
    System.out.println("solving\n"+formula.toString());

    // tree of atom-value guesses
    BinaryTree guessTree = new BinaryTree();
    // graph of atom-value pairs determined from known values
    ImplicationGraph implicationGraph = new ImplicationGraph();
    // current accepted atom-value pairs
    Map<Atom, Boolean> knownValues = new HashMap<>();

    main:
    while (true) {
      // check for unsat state
      if (guessTree.root.isUnsat())
        return false;

      unitPropagation: // loop allows multiple atoms to be determined via unit propagation
      while (true) {
        for (Clause clause : formula) {
          if (clause.isSatisfied())
            continue;
          if (clause.checkIfSat(knownValues)) {
            clause.setSatisfied(true);
            continue;
          }

          List<Literal> unknownLiterals = clause.unknownLiterals(knownValues);
          if (unknownLiterals.size() == 0) {
            if (clause.checkIfSat(knownValues)) {
              clause.setSatisfied(true);
              continue;
            } else {
              // conflict
              // undoes previous guess, makes a new one and corrects the map and graph accordingly
              System.out.println("reversing guess of " + guessTree.getAtom() + "=" + guessTree.getValue());
              guessTree = guessTree.reverseGuess(knownValues, implicationGraph);
              continue main;

            }
          }
          if (unknownLiterals.size() == 1) {
            // can determine last atom in clause via unit propagation
            Literal unknownLiteral = unknownLiterals.get(0);
            if (knownValues.containsKey(unknownLiteral.getAtom())) {
              //TODO: check if possible
              System.out.println("ERROR SHOULD NEVER OCCUR");
              // conflict
              // undoes previous guess, makes a new one and corrects the map and graph accordingly
              guessTree.reverseGuess(knownValues, implicationGraph);
              continue main;
            } else {
              // no conflict
              System.out.println("unit propagation, " + clause + " with " + knownValues);
              implicationGraph.implies(clause, unknownLiteral.getAtom());
              if (unknownLiteral.isNegated())
                knownValues.put(unknownLiteral.getAtom(), false);
              else
                knownValues.put(unknownLiteral.getAtom(), true);
              clause.setSatisfied(true);
              continue unitPropagation;
            }
          }
        }
        break;
      }

      // check for solved state
      checkSolved:
      while (true) {
        for (Clause clause : formula)
          if (!clause.isSatisfied())
            break checkSolved;
        return true;
      }

      // pick & guess a variable
      for (Clause clause : formula) {
        if (clause.isSatisfied())
          continue;
        Atom unknownAtom = clause.unknownLiterals(knownValues).get(0).getAtom();
        guessTree = guessTree.addChild(unknownAtom, true);
        knownValues.put(unknownAtom, true);
        System.out.println("guessing "+unknownAtom+" is true");
        break;
      }
    }
  }
}
