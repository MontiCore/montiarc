/* (c) https://github.com/MontiCore/monticore */
package components.head.inheritance;

/**
* Valid component.
*/
component Container {
    port
        in String;
    
    component InnerComponent1 {
        port
            in Boolean b;
    }
    
    component InnerComponent2 extends InnerComponent1 {
        port
            in Boolean inBool;
    }
}
