package instantiation;

component InnerComponentWithFormalTypeParameters {
  component InnerWithFTP<T> {
    port in T tIn;  
  }
}