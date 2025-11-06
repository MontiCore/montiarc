/* (c) https://github.com/MontiCore/monticore */
component MissingPortType15 {

  port in int i;
  port out Missing o;

  automaton {
    initial state S;
    S -> S i / { o = i; }
  }

}
