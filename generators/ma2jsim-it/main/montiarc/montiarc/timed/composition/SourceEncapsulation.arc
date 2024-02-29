/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.composition;

import montiarc.timed.automata.Source;
import montiarc.types.OnOff;

component SourceEncapsulation {

  port out OnOff o;

  Source sub;

  sub.o -> o;
}
