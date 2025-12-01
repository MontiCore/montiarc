/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: Two ports with the same name i (multiple identifier with the
 * same name).
 */
component NameClashPortPort1 {

  port in int i;
  port in int i;
  port out int o;

  automaton {
    initial state S;
    S -> S [i > 1] i / { o = i; }
  }
}
