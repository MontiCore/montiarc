/* (c) https://github.com/MontiCore/monticore */
package montiarc.ma2jsim.test;

component Other(int first, int second, int withDefault = 3) {
  port <<sync>> in Integer i;
  port <<sync>> out Integer o;
  port <<sync>> out boolean b;

  <<sync>> automaton {
      // initial state to delay initial output
      initial state S;

      // emit received message
      S -> S [ i > 0 ] / { o = i; b = true; };
      S -> S [ i <= 0 ] / { o = i; b = false; };
    }
}
