/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component BooleanOut {
  port out boolean p;

  <<sync>> automaton {
    initial state S;
    S -> S / { p = true; };
  }
}
