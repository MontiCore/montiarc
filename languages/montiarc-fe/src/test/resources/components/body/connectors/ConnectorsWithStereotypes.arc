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
    in Integer myOut;
  
  <<fakeNews>> connect myIn -> a.input; 
  <<realNews>> connect a.output -> myOut;
  
}