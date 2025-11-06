/* (c) https://github.com/MontiCore/monticore */
component MissingPortType13 {

  port in Missing i;
  port out int o;

  automaton {
    initial state S;
    S -> S [i] i;
  }

}
