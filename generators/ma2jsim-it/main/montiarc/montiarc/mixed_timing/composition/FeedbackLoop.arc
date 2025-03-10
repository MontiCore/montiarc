/* (c) https://github.com/MontiCore/monticore */
package montiarc.mixed_timing.composition;

import montiarc.timed.automata.Source;
import montiarc.sync.automata.Medium;
import montiarc.mixed_timing.automata.DelaySyncMedium;
import montiarc.types.OnOff;

component FeedbackLoop {

  port sync out OnOff o;

  Source source;
  DelaySyncMedium medium2x2;
  Medium medium;
  source.o -> medium2x2.inEvent;
  medium.o -> medium2x2.inSync;

  medium2x2.oSync -> o, medium.i;
}
