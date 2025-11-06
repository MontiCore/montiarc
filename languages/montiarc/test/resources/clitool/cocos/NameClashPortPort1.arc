/* (c) https://github.com/MontiCore/monticore */
component NameClashPortPort1 {

  port in int i;
  port in int i;
  port out int o;

  automaton {
    initial state S;
    S -> S [i > 1] i / { o = i; }
  }
}
