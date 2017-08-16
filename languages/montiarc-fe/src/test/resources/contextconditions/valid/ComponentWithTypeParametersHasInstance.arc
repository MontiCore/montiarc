package valid;

component ComponentWithTypeParametersHasInstance {
  autoconnect type;
  
  port in String pIn,
       out Integer pOut;

  component Inner<T> inner<String> {
    port in T tIn;
    port out Integer tOut;
  }
  
}