/* (c) https://github.com/MontiCore/monticore */
component NameClashParamParam(int p, int p) {

  port in int i;
  port out int o;

  int v = p;

  automaton {
    initial state S;
    S -> S [i > p] i / { o = p; }
  }
}
