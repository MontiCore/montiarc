/* (c) https://github.com/MontiCore/monticore */

/**
 * The model is invalid. Variable i is defined multiple times inside the
 * same scope, once as a comp variable, and once as a port. The tool
 * should report an error for i being defined multiple times inside the same
 * scope but no subsequent errors.
 */
component NameClashVarPort {

  int i = 1;

  port in int i;
  port out int o;

  automaton {
    initial state S;
    S -> S [i > 1] i / { o = i; };
  }
}
