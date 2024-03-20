/* (c) https://github.com/MontiCore/monticore */

/**
 * The model is invalid. Parameter p is defined multiple times inside the
 * same scope. The tool should report an error for p being defined multiple
 * times inside the same scope but no subsequent errors.
 */
component NameClashParamParam(int p, int p) {

  port in int i;
  port out int o;

  int v = p;

  automaton {
    initial state S;
    S -> S [i > p] i / { o = p; };
  }
}
