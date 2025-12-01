/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: A port and a field with the same name i (multiple identifier
 * with the same name).
 */
component NameClashPortVar {

  port in int i;
  port out int o;

  int i = 0;

  automaton {
    initial state S;
    S -> S [i > 1] i / { o = i; }
  }
}
