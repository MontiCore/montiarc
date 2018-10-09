package components.body.automaton.transition.guards;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


/*
 * Invalid model.
 * Uses undeclared fields in guards of transitions.
 *
 * @implements [Wor16] AR1: Names used in guards, valuations, and
 *    assignments exist in the automaton. (p. 102, Lst. 5.19)
 */
component GuardUsesUndeclaredField{

  Integer time;
  HashMap<String, String> strings;
  List<String> stringList;

  automaton Test{

    state Initial, Second, Third;
    initial Initial / {time=0, strings = new HashMap<String, String>(),
      stringList = new ArrayList<String>()};

    Initial -> Second [intVariable == 0];
      // Error: intVariable is not declared
    Second -> Third [undeclaredList.get(0).equals(anotherUndeclVar)
        && time == System.currentTimeMillis()];
      // Error: undeclaredList ist not declared
      // Error: anotherUndeclVar is not declared
  }
}