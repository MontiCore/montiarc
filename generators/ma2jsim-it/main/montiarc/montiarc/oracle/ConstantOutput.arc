/* (c) https://github.com/MontiCore/monticore */
package montiarc.oracle;

import montiarc.types.OnOff;

component ConstantOutput(int value) {
  port sync out int o;

  automaton {
    initial state S;
    S -> S / o = value;;
  }
}
