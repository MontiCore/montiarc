/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;

/** 
 * Valid model.
 */
component ConnectorsWithStereotypes {

  component A a {
    port 
      in Integer input,
      out Integer output;
  }

  port  
    in Integer myIn, 
    out Integer myOut;
  
  <<fakeNews>> connect myIn -> a.input; 
  <<realNews>> connect a.output -> myOut;
  
}
