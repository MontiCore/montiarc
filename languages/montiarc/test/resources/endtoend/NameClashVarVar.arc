/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: Two fields with the same name v (multiple identifier with the
 * same name).
 */
component NameClashVarVar {

  port in int i;
  port out int o;

  int v = 1;
  int v = 2;

  automaton {
    initial state S;
    S -> S [i > v] i / { o = v; }
  }
}
