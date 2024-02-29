/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import montiarc.sync.automata.Source;
import montiarc.types.OnOff;

component SourceEncapsulation {

  port out OnOff o;

  Source sub;

  sub.o -> o;
}
