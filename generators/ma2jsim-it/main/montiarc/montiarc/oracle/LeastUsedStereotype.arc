/* (c) https://github.com/MontiCore/monticore */
package montiarc.oracle;

import montiarc.types.OnOff;

<<oracle="leastUsed">>
component LeastUsedStereotype {
  port in OnOff i;
  port out int o;

  automaton {
    initial state S;
    S -> S i / o = 1;
    S -> S i / o = 2;
  }
}
