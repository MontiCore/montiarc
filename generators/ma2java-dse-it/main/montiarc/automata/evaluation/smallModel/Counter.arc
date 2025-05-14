/* (c) https://github.com/MontiCore/monticore */
package automata.evaluation.smallModel;

/**
 * small model for the evaluation
 */
component Counter {
  port sync in Double factor;
  port sync out Double out;

  Double counter = 0.0;

  <<delayed>> automaton{
    initial {out = 0.0;} state Idle;

    Idle -> Idle /{
      counter = counter + factor;
      out = counter;
    };
  }
}
