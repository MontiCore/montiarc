/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: A field and a port with the same name v (multiple identifier
 * with the same name).
 */
component NameClashVarPort {

  int v = 1;

  port in int v;
  port out int o;

  automaton {
    initial state S;
    S -> S [v > 1] v / { o = v; }
  }
}
