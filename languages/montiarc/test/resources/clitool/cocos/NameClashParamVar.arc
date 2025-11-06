/* (c) https://github.com/MontiCore/monticore */
component NameClashParamVar(int p) {

  port in int i;
  port out int o;

  int p = 0;

  automaton {
    initial state S;
    S -> S [i > p] i / { o = p; }
  }
}
