/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

component ForLoop {

  port in int i;
  port out int o;

  automaton {
    initial state S;
    S -> S [i == 1] i / {
      for (int a = 0; a < 10; a++) {
        o = a;
      }
    }
    S -> S [i == 2] i / {
      for (int a = 9; a >= 0; a--) {
        o = a;
      }
    }
    S -> S [i == 3] i / {
      for (int a = 0, b = 2; a < 10; a++, b *= 2) {
        o = b;
      }
    }
    S -> S [i == 4] i / {
      int a = 0;
      for (; a < 10; a++) {
        o = a;
      }
    }
    S -> S [i == 5] i / {
      for (int a = 0; ; a++) {
        if (!(a < 10)) break;
        o = a;
      }
    }
    S -> S [i == 6] i / {
      for (int a = 0; a < 10;) {
        o = a;
        a++;
      }
    }
  }
}
