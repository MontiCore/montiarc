/* (c) https://github.com/MontiCore/monticore */
package parser;
component Hierarchy {

  port in int i;
  port out String o;

  <<sync>> automaton {
    initial state A{
      initial state A1;
      state A2;
      state A3{
        initial state A31;
      };
    };
    state B{
    initial state B1;
    state B2;
    state B3;
    };
    state C{
      initial state C1{
        initial state C11;
      };
    };
  }
}
