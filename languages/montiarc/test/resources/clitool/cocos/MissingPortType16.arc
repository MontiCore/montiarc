/* (c) https://github.com/MontiCore/monticore */
component MissingPortType16 {

  port in Missing i;
  port out Missing o;

  automaton {
    initial state S;
    S -> S i / { o = i; }
  }

}
