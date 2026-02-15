/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

component WhileLoop {

  port in int i;
  port out int o;

  automaton {
    initial state S;
    S -> S [i == 1] i / {
      int a = 0;
      while (a < 10) {
        o = a;
        a++;
      }
    }
    S -> S [i == 2] i / {
      int a = 0;
      while (a < 20) {
        if (!(a < 10)) break;
        o = a;
        a++;
      }
    }
    S -> S [i == 3] i / {
      int a = 0;
      while (a < 10) o = a++;
    }
  }
}
