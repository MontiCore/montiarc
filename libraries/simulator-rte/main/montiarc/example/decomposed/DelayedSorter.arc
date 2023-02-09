/* (c) https://github.com/MontiCore/monticore */
package example.decomposed;

component DelayedSorter {
  port in Integer iIn;

  port out <<delayed>> Integer gtEq0;
  port out <<delayed>> Integer lt0;

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
