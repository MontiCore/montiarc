package components.head.inheritance;

import java.util.ArrayList;
import types.SuperCompWithGenerics;

/*
 * Valid model.
 */
component SubCompCorrect4<T, B>(ArrayList<T> l1, ArrayList<B> l2)
    extends SuperCompWithGenerics<T, B> {
  // Empty body
}