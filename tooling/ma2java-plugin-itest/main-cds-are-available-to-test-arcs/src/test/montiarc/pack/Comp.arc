/* (c) https://github.com/MontiCore/monticore */
package pack;

import pack.Types.Color;

component Comp {
  port in Color i;
  port out Color o;
  automaton {
    initial state X;
    X -> X i / { o = i; }
  }
}
