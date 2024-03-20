/* (c) https://github.com/MontiCore/monticore */

/**
 * The model is invalid. Variable v is defined multiple times inside the
 * same scope. The tool should report an error for v being defined multiple
 * times inside the same scope but no subsequent errors.
 */
component NameClashVar {

  port in int i;
  port out int o;

  int v = 1;
  int v = 2;

  automaton {
    initial state S;
    S -> S [i > v] i / { o = v; };
  }
}
