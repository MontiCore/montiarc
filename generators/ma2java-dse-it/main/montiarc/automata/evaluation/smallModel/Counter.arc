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
    initial state IdleS {
      initial state IdleInit {
        entry / { out = 0.0; }
      }
      state Idle;
    }

    IdleS -> Idle / {
      counter = counter + factor;
      out = counter;
    }
  }
}
