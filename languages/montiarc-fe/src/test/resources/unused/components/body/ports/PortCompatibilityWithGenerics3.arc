package components.body.ports;

/**
* Invalid component. See comments below.
*/
component PortCompatibilityWithGenerics3 {
  port 
    in Integer myInt,
    out Object myObj;

  component Buffer<T> {
    port
      in T input,
      out T buffered;
  }
  component Buffer<Integer> bInt;
  component Buffer<Object> bObj;
  component Buffer<String> bStr;
  
  connect myInt -> bInt.input;         // Int -> Int
  connect bInt.buffered -> bObj.input; // Int -> Obj
  connect bObj.buffered -> bStr.input; // Obj -> Str
                                       // invalid!    
  connect bStr.buffered -> myObj;      // Str -> Obj
}