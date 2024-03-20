/* (c) https://github.com/MontiCore/monticore */

/**
 * The model is invalid. Variable i is defined multiple times inside the
 * same scope, once as a port, and once as a comp variable. The tool
 * should report an error for i being defined multiple times inside the same
 * scope but no subsequent errors.
 */
component NameClashPortVar {

  port in int i;
  port out int o;

  int i = 0;

  automaton {
    initial state S;
    S -> S [i > 1] i / { o = i; };
  }
}
