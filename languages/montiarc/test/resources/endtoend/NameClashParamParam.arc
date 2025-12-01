/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: Two parameters with the same name p (multiple identifier with
 * the same name).
 */
component NameClashParamParam(int p, int p) {

  port in int i;
  port out int o;

  int v = p;

  automaton {
    initial state S;
    S -> S [i > p] i / { o = p; }
  }
}
