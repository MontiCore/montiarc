/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component CharWrapperOut {
  port sync out Character p;

  automaton {
    initial state S;
    S -> S / { p = 'a'; }
  }
}
