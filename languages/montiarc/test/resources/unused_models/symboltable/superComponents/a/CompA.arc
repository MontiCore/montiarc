/* (c) https://github.com/MontiCore/monticore */
package a;

/*
 * Valid model.
 */
component CompA extends SuperComponentSamePackage {
    port
        in String sth,

        // overwrite a port from the super component
        in Integer s1;

}
