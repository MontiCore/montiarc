package components.head.inheritance;

/**
* Invalid component. A inner component must not extend its defining component.
* @Implements [Hab16] R12: An inner component type definition must not 
* extend the component type in which it is defined. (p. 68, lst. 3.47)
*
*/
component OuterComponent {

    port
        in String;
        
    component InnerComponent extends OuterComponent { //invalid
        port
            in Integer;
    
    }
    
    component InnerComponent2 {
        port
            in Boolean;
            
        component InnerInnerComponent extends OuterComponent { //invalid
            port
                in Float;
        }
    }

}