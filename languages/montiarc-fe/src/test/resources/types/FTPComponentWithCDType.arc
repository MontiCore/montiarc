package types;

import types.Types.Car;
/**
 * Valid model. 
 */
component FTPComponentWithCDType {
  port in  Car cIn,
       out Car cOut;

  component GenericTransformer<Car> transformer [tOut -> cOut];
  
  connect cIn -> transformer.tIn;
}