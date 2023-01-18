/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

/**
 * Valid model.
 */
component GenericCompWithInnerGenericComp<T> {

    port
        in T input;

    component InnerGeneric<T> {
        port
            in T input;
    }

    component InnerGeneric<T> inner;

    connect input -> inner.input;

}
