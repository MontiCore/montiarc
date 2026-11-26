/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import java.util.Optional;
import montiarc.types.OnOff;

component SendNoOptionalMessages {
  port sync in OnOff p;
  port sync out Optional<OnOff> o;

  compute { }
}
