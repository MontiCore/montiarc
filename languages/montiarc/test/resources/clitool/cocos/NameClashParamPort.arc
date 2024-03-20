/* (c) https://github.com/MontiCore/monticore */

/**
 * The model is invalid. Variable p is defined multiple times inside the
 * same scope, once as a parameter, and once as a port. The tool should report
 * an error for p being defined multiple times inside the same scope but no
 * subsequent errors.
 */
component NameClashParamPort(int p) {

  port in int p;
  port out int o;

  int v = p;

  automaton {
    initial state S;
    S -> S [p > p] p / { o = p; };
  }
}
