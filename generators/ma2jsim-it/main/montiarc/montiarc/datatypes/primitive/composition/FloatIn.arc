/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component FloatIn {
  port in float p;

  <<sync>> automaton {
    initial state S;
    S -> S / { float foo = p; };
  }
}
