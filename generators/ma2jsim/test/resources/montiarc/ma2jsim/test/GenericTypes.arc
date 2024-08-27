/* (c) https://github.com/MontiCore/monticore */
package montiarc.ma2jsim.test;

import java.util.Optional;

component GenericTypes {
  port <<sync>> in Optional<Integer> iOpt;
  port <<sync>> out int o;

  Optional<Integer> iBuf = Optional.empty();

  <<sync>> automaton {
      initial state S;

      // emit received message
      S -> S [ iOpt.isPresent() ] / { iBuf = iOpt; o = iBuf.get(); };
      S -> S [ iOpt.isEmpty() ] / { o = iBuf.orElse(-1); };
    }
}
