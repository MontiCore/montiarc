/* (c) https://github.com/MontiCore/monticore */
package montiarc.ma2jsim.test;

component Source {
  port out Integer o;

  <<sync>> automaton {
    initial {
      o = 0;
    } state S;
    S -> S / { o = 1; };
  }
}
