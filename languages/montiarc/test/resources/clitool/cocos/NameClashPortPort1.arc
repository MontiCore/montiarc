/* (c) https://github.com/MontiCore/monticore */

/**
 * The model is invalid. Incoming port i is defined multiple times inside the
 * same scope. The tool should report an error for i being defined multiple
 * times inside the same scope but no subsequent errors.
 */
component NameClashPortPort1 {

  port in int i;
  port in int i;
  port out int o;

  automaton {
    initial state S;
    S -> S [i > 1] i / { o = i; };
  }
}
