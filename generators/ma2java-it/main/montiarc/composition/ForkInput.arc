/* (c) https://github.com/MontiCore/monticore */
package composition;

import Types.OnOff;
import compute.Forward;

component ForkInput {

  port in OnOff i;
  port out OnOff o1, o2;

  Forward c1, c2;

  i -> c1.i;
  i -> c2.i;
  c1.o -> o1;
  c2.o -> o2;
}
