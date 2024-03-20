/* (c) https://github.com/MontiCore/monticore */

/**
 * The model is invalid. Variable p is defined multiple times inside the
 * same scope, once as a parameter, and once as a comp variable. The tool
 * should report an error for p being defined multiple times inside the same
 * scope but no subsequent errors.
 */
component NameClashParamVar(int p) {

  port in int i;
  port out int o;

  int p = p;

  automaton {
    initial state S;
    S -> S [i > p] i / { o = p; };
  }
}
