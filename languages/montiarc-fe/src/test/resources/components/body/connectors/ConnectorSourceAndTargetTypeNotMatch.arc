package components.body.connectors;

/**
* Invalid component. See comments below.
* Also see https://git.rwth-aachen.de/monticore/montiarc/core/issues/243
*
* @implements [Hab16] R8: The target port in a connection has to be compatible
* to the source port, i.e., the type of the target port is identical or a
* supertype of the source port type. (p. 66, lst. 3.43)
*/
component ConnectorSourceAndTargetTypeNotMatch {
  
  port 
    in Integer myInt,
    out Object myObj;
    
  component Buffer<Integer> bInt;
  component Buffer<Object> bObj;
  component Buffer<String> bStr;
  
  connect myInt -> bInt.input;
  connect bInt.buffered -> bObj.input; // ERROR: #243
  connect bObj.buffered -> bStr.input; //incompatible types 
                                       //Object and String
  connect bStr.buffered -> myObj; //ERROR: #243
  
  component Buffer<T> {
    port
      in T input,
      out T buffered;
  }

}
