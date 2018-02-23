package a;
import java.lang.String;
import components.body.subcomponents._subcomponents.HasGenericInputAndOutput;
import components.body.subcomponents._subcomponents.SingleIntegerParameter;
import components.body.subcomponents._subcomponents.SingleStringParameter;
/*
 * Invalid model
 *
 * Formerly named "CG13" in MontiArc3.
 *
 * @implements TODO
 * TODO Add test
 */
component WrongSubcomponentAndGenericParameters {
  component CG13false1<String> f1;
      //ERROR: Number of assigned generic type parameters does not match
      //        the number of required generic type parameter
  component SingleIntegerParameter("5") f2;
      //ERROR: Type 'java.lang.String' of value "5" for parameter 'x'
      //        does not match required type 'java.lang.Integer'
  
  component HasGenericInputAndOutput<String> t1; // Correct
  component SingleStringParameter(5) t2;
      //ERROR: Type 'java.lang.Integer' of value '5' for parameter 'x'
      //        does not match required type 'java.lang.String'
}