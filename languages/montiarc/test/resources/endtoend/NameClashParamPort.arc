/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: A parameter and a port with the same name p (multiple
 * identifier with the same name).
 */
component NameClashParamPort(int p) {

  port in int p;
  port out int o;

  int v = 0;

  automaton {
    initial state S;
    S -> S [p > p] p / { o = p; }
  }
}
