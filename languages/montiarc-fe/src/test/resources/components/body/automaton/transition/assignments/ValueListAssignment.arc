/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.transition.assignments;

/*
 * Invalid component. Uses a ValueList in the only transition.
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