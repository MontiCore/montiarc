/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import montiarc.types.OnOff;

component GenericSequentialComposition {

  port sync in OnOff i;
  port sync out OnOff o;

  montiarc.sync.automata.GenericForwarder<OnOff> sub1;
  montiarc.sync.compute.GenericForwarder<montiarc.types.OnOff> sub2;

  i -> sub1.i;

  sub1.o -> sub2.i;

  sub2.o -> o;
}
