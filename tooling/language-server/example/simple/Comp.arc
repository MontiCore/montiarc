/* (c) https://github.com/MontiCore/monticore */
package simple;

component Comp {
  port in int p1;
  port out int p2;
  port in AType p3;

  automaton {
    initial state A;
    state B;

    A -> B;
  }
}