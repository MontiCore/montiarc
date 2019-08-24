/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.transition.assignments;

/*
  Invalid component. Uses a ValueList in the only transition.

  @implements TODO: Unreferenced in Literatur
*/
component ValueListAssignment {

    port 
        out Boolean b;
    
    automaton ValueListAssignment {
      state S;
      initial S;
    
      S -> S / {b = [true, false, true]};
    }
}
