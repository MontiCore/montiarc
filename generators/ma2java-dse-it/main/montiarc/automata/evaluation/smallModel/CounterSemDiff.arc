/* (c) https://github.com/MontiCore/monticore */
package automata.evaluation.smallModel;
/**
 * small model for the evaluation
 */
component CounterSemDiff {
  port sync in Double factor;
  port sync in Boolean chaos;
  port <<delayed>> sync out Double out;

  Double counter = 0.0;

  automaton{
    initial {out = 0.0;} state Idle;
    state Chaos;

     Idle -> Idle [chaos == false]/{
      counter = counter + factor;
      out = counter;
    };

    Idle -> Idle [chaos == true && counter < 1]/{
      out = counter;
    };

    Idle -> Chaos [chaos == true && counter >= 1]/{
      counter = 0.0;
      out = counter;
    };

    Chaos -> Chaos /{
      counter = factor * 1.5;
      out = counter;
    };
  }
}
