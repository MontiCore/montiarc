/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.transition.assignments;

/**
* Valid model.
*/

component CallOfOverloadedMethods {

  Long l;

  automaton {
    state S1, S2;

    initial S1 / {l = new Long(5), call l.valueOf("5l", 6)};
  }


}
