/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: Two ports with the same name o (multiple identifier with the
 * same name).
 */
component NameClashPortPort2 {

  port in int i;
  port out int o;
  port out int o;

  automaton {
    initial state S;
    S -> S [i > 1] i / { o = i; }
  }
}
