/* (c) https://github.com/MontiCore/monticore */
import subcomponentDefinitions.*;

/**
 * Invalid model.
 */
component TooManyAndWrongBindings {
  Complex complex(2, 2.3, true, false);
  Complex complex(2, a=2.3, c=true, b=false);
}
