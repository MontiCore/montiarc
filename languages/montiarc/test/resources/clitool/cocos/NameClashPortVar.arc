/* (c) https://github.com/MontiCore/monticore */
component NameClashPortVar {

  port in int i;
  port out int o;

  int i = 0;

  automaton {
    initial state S;
    S -> S [i > 1] i / { o = i; }
  }
}
