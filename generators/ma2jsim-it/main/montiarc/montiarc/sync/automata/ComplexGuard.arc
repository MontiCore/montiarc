/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import java.util.Map;
import montiarc.types.Person;

/* This test does not come with java i/o tests, but checks whether the complex guard expression compiles */
component ComplexGuard {
  port <<sync>> in Map<String, String> nameToWhatever;
  port <<sync>> in Person person;
  port out String o;

  <<sync>> automaton {
      initial state S;
      // deliberately chains ASTFieldAccessExpressions
      S -> S [
        nameToWhatever.containsKey(person.name) &&
        nameToWhatever.get(person.name)
                      .equals("Foo")
      ] / o = "success";;
    }
}
