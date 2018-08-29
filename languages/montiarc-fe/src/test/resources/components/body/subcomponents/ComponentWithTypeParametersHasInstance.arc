package components.body.subcomponents;

/**
 * Valid model.
 */
component ComponentWithTypeParametersHasInstance {
  autoconnect type;
  
  port in String pIn,
       out Integer pOut;

  component Inner<T> {
    port in T tIn;
    port out Integer tOut;
  }

  component Inner<String>;
  
}