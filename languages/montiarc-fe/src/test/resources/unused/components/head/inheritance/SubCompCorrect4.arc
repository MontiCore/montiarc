package components.head.inheritance;

import java.util.ArrayList;
import types.SuperCompWithGenerics;

/**
* Correct component.
* @implements [Hab16] R14: Components that inherit from a parametrized 
* component provide configuration parameters with the same types, 
* but are allowed to provide more parameters. (p. 69, lst. 3.49) 
*/
component SubCompCorrect4<T, B>(ArrayList<T> l1, ArrayList<B> l2) extends SuperComp2<T, B> {

}