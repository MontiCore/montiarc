/* (c) https://github.com/MontiCore/monticore */

import subcomponentDefinitions.*;

/**
 * Invalid model.
 */
component TooManyFeatureAssignments {
  Simple c1(true, f1=true, f2=true);
  Simple c2(true, f2=true, f1=true);
}
