/* (c) https://github.com/MontiCore/monticore */
package montiarc.oracle;

import montiarc.types.OnOff;

<<oracle="first">>
component CompositionLeastUsedStereotype {
  port in OnOff i;
  port out int o;

  LeastUsedStereotype sub;
  i -> sub.i;
  sub.o -> o;
}
