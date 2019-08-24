/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

import java.lang.String;
import components.body.subcomponents._subcomponents.HasGenericInputAndOutputPort;
import components.body.subcomponents._subcomponents.SingleIntegerParameter;
import components.body.subcomponents._subcomponents.SingleStringParameter;
import components.body.subcomponents._subcomponents.AtomicComponent;
/*
 * Invalid model
 *
 * Formerly named "CG13" in MontiArc3.
 *
 * @implements [Hab16] R9: If a generic component type is instantiated
 *  as a subcomponent, all generic parameters have to be assigned.
 *  (p. 66, lst. 3.44)
 * @implements [Hab16] R10: If a configurable component is instantiated as a
 *  subcomponent, all configuration parameters have to be assigned.
 *  (p. 67, lst. 3.45)
 */
component WrongSubcomponentAndGenericParameters {
  component AtomicComponent<String> f1;
      //ERROR: Number of assigned generic type parameters does not match
      //        the number of required generic type parameter
  component SingleIntegerParameter("5") f2;
      //ERROR: Type 'java.lang.String' of value "5" for parameter 'x'
      //        does not match required type 'java.lang.Integer'
  
  component HasGenericInputAndOutputPort<String> t1; // Correct
  component SingleStringParameter(5) t2;
      //ERROR: Type 'java.lang.Integer' of value '5' for parameter 'x'
      //        does not match required type 'java.lang.String'
}
