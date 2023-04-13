/* (c) https://github.com/MontiCore/monticore */
package composition;

component Counter {
  port in iIn;
  port out count;
  Integer cnt = 0;

  automaton {
    initial state S;

    S -> S / {
      cnt = cnt + 1;
      count = cnt;
    }
  }
}
