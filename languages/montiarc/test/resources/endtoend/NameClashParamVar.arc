/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: A field and a port with the same name p (multiple identifier
 * with the same name).
 */
component NameClashParamVar(int p) {

  port in int i;
  port out int o;

  int p = 0;

  automaton {
    initial state S;
    S -> S [i > p] i / { o = p; }
  }
}
