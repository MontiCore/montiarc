/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component BooleanIn {
  port in boolean p;

  <<sync>> automaton {
    initial state S;
    S -> S / { boolean foo = p; };
  }
}
