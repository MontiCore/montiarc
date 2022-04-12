/* (c) https://github.com/MontiCore/monticore */
package testing.structure.composed;

//import java.lang.String;
import testing.structure.*;

/**
 * a simple composed component, that only contains one subcomponent and one port-forward
 */
component OutputPortForward {

  port out int outputValue;

  Ones provider;

  provider.outputValue -> outputValue;
}