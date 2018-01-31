package component.body.autoinstantiate;

/** 
 * Valid model.
 */
component InnerComponentWithFormalTypeParameters {
  component InnerWithFTP<T> {
    port in T tIn;  
  }
}