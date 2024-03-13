/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import montiarc.sync.automata.Delay;
import montiarc.sync.automata.Source;
import montiarc.types.OnOff;

component Deploy {

  Delay delay;
  delay.o -> delay.i;
  Source source;

  delay.o -> parallel.i1;
  source.o -> parallel.i2;
  ParallelComposition parallel;

}
