/* (c) https://github.com/MontiCore/monticore */
component MissingPortType14 {

  port in Missing i;
  port out int o;

  automaton {
    initial state S;
    S -> S i / { o = i; }
  }

}
