/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: Two type parameters with the same name T (multiple
 * identifier with the same name).
 */
component NameClashTypeParam<T, T>(T tp) {

  port in T i;
  port out T o;

  T tv = tp;

  automaton {
    initial state S;
    S -> S i / { o = i; }
  }

}
