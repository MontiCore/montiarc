/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.composition;

import montiarc.timed.automata.Source;
import montiarc.types.OnOff;

component Deploy {

  Source source;

  source.o -> parallel.i1;
  source.o -> parallel.i2;

  ParallelComposition parallel;

}
