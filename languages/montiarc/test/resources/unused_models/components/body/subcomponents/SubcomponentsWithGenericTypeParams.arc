/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

import components.body.subcomponents._subcomponents.*;

/**
* Invalid model.
*
* @implements [Hab16] R9: If a generic component type is instantiated
* as a subcomponent, all generic parameters have to be assigned. (p. 66, lst. 3.44)
*/
component SubcomponentsWithGenericTypeParams<T>(int myInt, String myString) {
    port
        in String s1;

    component SimpleGenericComponent gcWrong1; //wrong needs two type params

    component SimpleGenericComponent<T> gcWrong2; //wrong needs two type params

    component SimpleGenericComponent<T,T,String> gcWrong3; //wrong needs two type params

    component SimpleGenericComponent<T, String> gcCorrect1; //correct

    component SimpleGenericComponent<Integer, String> gcCorrect2; //correct


    connect s1 -> gcWrong1.stringIn;
    connect s1 -> gcWrong2.stringIn;
    connect s1 -> gcWrong3.stringIn;
    connect s1 -> gcCorrect1.stringIn;
    connect s1 -> gcCorrect2.stringIn;

}
