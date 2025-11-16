/* (c) https://github.com/MontiCore/monticore */
package montiarc.ma2jsim.test;

component Source {
  port sync out Integer o;

  automaton {
    initial state S {
      entry / {
        o = 0;
      }
      -> / { o = 1; }
    }
  }
}
