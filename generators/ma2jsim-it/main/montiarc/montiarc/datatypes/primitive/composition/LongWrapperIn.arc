/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component LongWrapperIn {
  port sync in Long p;

  automaton {
    initial state S;
    S -> S / { long foo = p; };
  }
}
