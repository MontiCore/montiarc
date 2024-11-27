/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

component Assert<T>(java.util.function.Consumer<T> assertion) {
  port in T actual;

  <<timed>> automaton {
    initial state S;
    S -> S actual / {
      assertion.accept(actual);
    };
  }
}
