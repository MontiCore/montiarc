/* (c) https://github.com/MontiCore/monticore */
package automata.evaluation.smallModel;
/**
 * small model for the evaluation
 */
component CounterSemDiff {
  port sync in Double factor;
  port sync in Boolean chaos;
  port sync out Double out;

  Double counter = 0.0;

  <<delayed>> automaton{
    initial state IdleS {
      initial state IdleInit {
        entry / { out = 0.0; }
      }
      state Idle;
    }


    state Chaos;

     IdleS -> Idle [chaos == false]/{
      counter = counter + factor;
      out = counter;
    }

    IdleS -> Idle [chaos == true && counter < 1]/{
      out = counter;
    }

    IdleS -> Chaos [chaos == true && counter >= 1]/{
      counter = 0.0;
      out = counter;
    }

    Chaos -> Chaos /{
      counter = factor * 1.5;
      out = counter;
    }
  }
}
