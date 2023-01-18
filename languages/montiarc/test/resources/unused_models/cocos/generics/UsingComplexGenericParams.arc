/* (c) https://github.com/MontiCore/monticore */
package components.head.generics;

import java.util.*;

/**
 * Valid model.
 */
component UsingComplexGenericParams<K, V> {

    component ComplexGenericParams<K, V> (new int[]{1, 2, 3}, new HashMap<List<K>, List<V>>()) cp;

}
