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
 * @implements TODO Literature
 * TODO Add test
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