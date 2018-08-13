package components.head.generics;

import java.util.*;

component AssignsWrongComplexTypeArgToSuperComp<K extends ArrayList<Integer>>
    extends ComplexFormalTypeParameter<K>{
  // Error: Type of K is not compatible to ArrayList<String>
}