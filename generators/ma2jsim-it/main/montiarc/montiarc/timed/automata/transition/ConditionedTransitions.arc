/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import montiarc.types.NumberSign;

component ConditionedTransitions {
  port in int i;
  port out NumberSign o;

  <<timed>> automaton {
    initial state S;

    S -> S [i > 0] i / { o = NumberSign.POSITIVE; };
    S -> S [i < 0] i / { o = NumberSign.NEGATIVE; };
    S -> S [i == 0] i / { o = NumberSign.ZERO; };
  }
}
