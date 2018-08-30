package components.body.subcomponents;

/**
 * Invalid model.
 * Inner components get instantiated automatically.
 * Hence, they cannot yield generic type parameters.
 *
 * @implements No literature reference
 */
component ComponentWithTypeParametersLacksInstance {
  
  port 
    in String pIn,
     out String pOut;
       
  component Inner<T> {
    port in T tIn;
    port out T tOut;
  }
}