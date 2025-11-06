/* (c) https://github.com/MontiCore/monticore */
component NameClashParamPort(int p) {

  port in int p;
  port out int o;

  int v = 0;

  automaton {
    initial state S;
    S -> S [p > p] p / { o = p; }
  }
}
