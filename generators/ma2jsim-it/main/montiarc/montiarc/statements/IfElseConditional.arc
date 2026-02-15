/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

component IfElseConditional {

  port in int i;
  port out int o;

  automaton {
    initial state S;
    S -> S i / {
      if (i >= 0) {
        if (i == 1) o = 1;
        if (i == 2) { o = 2; } else if (i == 3) { o = 3; };
        if (i == 4 || i == 5) if (i == 4) o = 4; else o = 5;
      } else {
        o = -1;
      }
    }
  }
}
