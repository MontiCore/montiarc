package components.head.generics;

/*
 * Invalid model.
 */
component AssignsWrongComplexTypeArgToSuperComp<K extends ArrayList<Integer>>
    extends ComplexFormalTypeParameter<K>{
  // Error: Type of K does not fulfill the upper bound ArrayList<String>
}
