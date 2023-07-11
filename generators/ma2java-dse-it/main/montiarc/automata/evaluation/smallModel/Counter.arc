/* (c) https://github.com/MontiCore/monticore */
package evaluation.smallModel;

/**
 * small model for the evaluation
 */
component Counter {
  port <<sync>> in Double factor;
  port <<sync,delayed>> out Double out;

  Double counter = 0.0;

  automaton{
    initial {out = 0.0;} state Idle;

    Idle -> Idle /{
      counter = counter + factor;
      out = counter;
    };
  }
}
