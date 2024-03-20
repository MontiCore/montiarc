/* (c) https://github.com/MontiCore/monticore */

/**
 * The model is invalid. Type parameter T is defined multiple times inside the
 * same scope. The tool should report an error for T being defined multiple
 * times inside the same scope but no subsequent errors.
 */
component NameClashTypeParam<T, T>(T tp) {

  port in T i;
  port out T o;

  T tv = tp;

  automaton {
    initial state S;
    S -> S i / {o = i; };
  }

}
