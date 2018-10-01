package types;

import types.Types.Car;

/**
 * Valid model. 
 */
component ComponentWithCDType {
  port in  Car cIn,
       out Car cOut;

  component CarTransformer transformer [tOut -> cOut];
  
  connect cIn -> transformer.tIn;
}