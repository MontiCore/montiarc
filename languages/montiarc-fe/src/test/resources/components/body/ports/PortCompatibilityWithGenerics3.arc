package components.body.ports;

/**
* Invalid model.
*
* @implements [Hab16] R8: The target port in a connection has to be compatible
*                           to the source port, i.e., the type of the target
*                           port is identical or a supertype of the source
*                           port type. (p.66, Lst. 3.43)
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