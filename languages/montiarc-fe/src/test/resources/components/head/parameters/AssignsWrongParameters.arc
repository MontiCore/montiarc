/* (c) https://github.com/MontiCore/monticore */
package components.head.parameters;

import components.head.parameters.CompWithIntegerParameter;
import components.body.subcomponents._subcomponents.AtomicComponent;
/*
 * Invalid model.
 *
 * Formerly named "CG12" in MontiArc3.
 *
 * @implements No literature reference
 * TODO Review literature
 */
component AssignsWrongParameters {

  component CompWithIntegerParameter(5) correct;
  
  component CompWithIntegerParameter wrong1;
  // ERROR: Required configuration parameter
  //         'myConfig' of type 'java.lang.Integer' is missing
  
  component AtomicComponent(5) wrong2;
    //ERROR: Component 'AtomicComponent' has no configuration parameter of type
    //        'java.lang.Integer'
  
  component AtomicComponent wrong3; //Correct
  
  component ParameterAmbiguous("asdf","4") wrong4;
    //ERROR: Missing required parameter 'sth' of type 'java.lang.Integer'
    //ERROR: Given value "asdf" for parameter 'name' is not of type 'java.lang.Integer'
    // TODO: Correct errors?
}
