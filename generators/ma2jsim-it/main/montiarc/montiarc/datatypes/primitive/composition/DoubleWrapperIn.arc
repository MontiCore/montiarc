/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component DoubleWrapperIn {
  port sync in Double p;

  automaton {
    initial state S;
    S -> S / { double foo = p; };
  }
}
