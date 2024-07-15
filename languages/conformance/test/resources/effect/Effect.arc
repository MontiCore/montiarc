/* (c) https://github.com/MontiCore/monticore */
package effect;

component Effect {

  port in int input;
  port out int output;

  automaton {
    initial {} state S;
    S -> S / {output = input;};
    S -> S [input==1337] / {output = 4000;};
  }
}
