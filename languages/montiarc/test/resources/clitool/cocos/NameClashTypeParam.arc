/* (c) https://github.com/MontiCore/monticore */
component NameClashTypeParam<T, T>(T tp) {

  port in T i;
  port out T o;

  T tv = tp;

  automaton {
    initial state S;
    S -> S i / {o = i; }
  }

}
