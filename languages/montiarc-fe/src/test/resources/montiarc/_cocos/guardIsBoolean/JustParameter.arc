/* (c) https://github.com/MontiCore/monticore */
package guardIsBoolean;

  // valid model
component JustParameter (boolean free) {

  automaton {
    initial state Begin;
    state End;

    Begin -> End [free];
  }

}