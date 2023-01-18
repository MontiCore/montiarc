/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.transition.guards;

/*
* Invalid model.
*
* @implements [Wor16] AC5: The automaton’s valuations and assignments use
* only allowed Java/P modeling elements. (p.100, Lst. 5.15)
*/
component GuardWithInstanceOfExpression {

    port
        in Integer[] values,
        out Integer result;

    Integer storage;

    automaton InstanceOfAndObjectInstantiation{
      state S4;
      state S1, S2;
      initial S1 / {result = 255};

      S1->S2 [values.length == 5 && storage == 0] / {storage = new Integer(0)};

      // 1 error: Guard uses instanceof expression
      S2->S1 [storage instanceof Object && storage == 5] / {result = storage};
    }
}
