/* (c) https://github.com/MontiCore/monticore */
package guardIsBoolean;

  // valid model
component JustVariable {

  boolean free = true;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [free];
  }

}