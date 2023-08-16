/* (c) https://github.com/MontiCore/monticore */
package montiarc.ma2jsim.test;

component Source {
  port <<sync>> out Integer o;

  automaton {
    initial {
      o = 0;
    } state S;
    S -> S / { o = 1; };
  }
}
