/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

component Forward<T> {
  port in T pIn,
       out T pOut;

  automaton {
    initial state S;
    S -> S pIn / {
      T intermediate = pIn;
      pOut = intermediate;
    };
  }
}
