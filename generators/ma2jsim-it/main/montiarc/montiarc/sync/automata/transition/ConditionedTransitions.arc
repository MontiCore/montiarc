/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.transition;

import montiarc.types.NumberSign;

component ConditionedTransitions {
  port sync in int i;
  port sync out NumberSign o;

  automaton {
    initial state S;

    S -> S [i > 0] / { o = NumberSign.POSITIVE; };
    S -> S [i < 0] / { o = NumberSign.NEGATIVE; };
    S -> S [i == 0] / { o = NumberSign.ZERO; };
  }
}
