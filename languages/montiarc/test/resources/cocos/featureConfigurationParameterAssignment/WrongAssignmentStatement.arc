/* (c) https://github.com/MontiCore/monticore */

import subcomponentDefinitions.*;

/**
 * Invalid model.
 */
component WrongAssignmentStatement {
  Simple c1(false, Simple.f1=true);
  Simple c2(false, true=f1);
}
