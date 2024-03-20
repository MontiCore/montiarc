/* (c) https://github.com/MontiCore/monticore */

/**
 * The model is invalid. Outgoing port o is defined multiple times inside the
 * same scope. The tool should report an error for o being defined multiple
 * times inside the same scope but no subsequent errors.
 */
component NameClashPortPort2 {

  port in int i;
  port out int o;
  port out int o;

  automaton {
    initial state S;
    S -> S [i > 1] i / { o = i; };
  }
}
