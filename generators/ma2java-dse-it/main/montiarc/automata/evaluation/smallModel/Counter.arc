/* (c) https://github.com/MontiCore/monticore */
package automata.evaluation.smallModel;

/**
 * small model for the evaluation
 */
component Counter {
  port in Double factor;
  port <<delayed>> out Double out;

  Double counter = 0.0;

  <<sync>> automaton{
    initial {out = 0.0;} state Idle;

    Idle -> Idle /{
      counter = counter + factor;
      out = counter;
    };
  }
}
