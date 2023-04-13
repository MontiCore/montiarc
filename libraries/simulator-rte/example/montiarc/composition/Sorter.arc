/* (c) https://github.com/MontiCore/monticore */
package composition;

component Sorter {
  port in Integer iIn;

  port out Integer gtEq0;
  port out Integer lt0;

  automaton {
    initial state S;

    S -> S [iIn >= 0] / {
      gtEq0 = iIn;
    }

    S -> S [iIn < 0] / {
      lt0 = iIn;
    }
  }
}
