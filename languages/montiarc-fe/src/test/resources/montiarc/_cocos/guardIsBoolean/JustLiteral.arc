/* (c) https://github.com/MontiCore/monticore */
package guardIsBoolean;

  // valid model
component JustLiteral {

  automaton {
    initial state Begin;
    state End;

    Begin -> End [false];
  }

}