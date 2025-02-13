/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component FloatIn {
  port sync in float p;

  automaton {
    initial state S;
    S -> S / { float foo = p; };
  }
}
