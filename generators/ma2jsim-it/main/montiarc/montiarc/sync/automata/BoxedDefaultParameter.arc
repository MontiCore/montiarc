/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

component BoxedDefaultParameter(
  Double param = 5,
  java.lang.Number numberParam = 5
) {

  port sync out Double o;
  port sync out java.lang.Number n;

  automaton {
    initial state S;

    S -> S / {
      o = param;
      n = numberParam;
    }
  }
}
