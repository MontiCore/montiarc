/* (c) https://github.com/MontiCore/monticore */
package components.body.autoconnect;

/**
 * Valid model.
 */
component AutoConnectGenericUsageHierarchy {

    autoconnect type;

    port
        in String strIn,
        out Object objOut;


    component Inner<T> sInner<Object> {
        port
            in T tIn,
            out T tOut;
    }

}
