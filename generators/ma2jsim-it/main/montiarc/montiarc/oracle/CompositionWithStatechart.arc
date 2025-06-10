/* (c) https://github.com/MontiCore/monticore */
package montiarc.oracle;

import montiarc.types.OnOff;

component CompositionWithStatechart {
  port in OnOff i;
  port out int o;

  WithStatechart sub;
  i -> sub.i;
  sub.o -> o;
}
