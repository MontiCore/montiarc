package invalid;

component ComponentWithTypeParametersLacksInstance {
  
  port in String pIn,
       out String pOut;
       
  component Inner<T> {
    port in T tIn;
    port out T tOut;
  }
  
}