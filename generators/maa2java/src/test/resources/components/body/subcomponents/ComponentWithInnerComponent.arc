package components.body.subcomponents;

import components.body.subcomponents._subcomponents.AtomicComponent;

/**
 * Valid model. Used by 
 */
component ComponentWithInnerComponent {

    port
        in String strIn,
        out String strOut;

    component AtomicComponent myReference;

    component InnerComponent {
    
        port
            in String strIn,
            out String strOut;

        component AtomicComponent myReference;
        
        component InnerInnerComponent {
            port
                in String strIn,
                out String strOut;

           component AtomicComponent myReference;
        }

        connect innerInnerComponent.strOut -> strOut; 
        connect strIn -> innerInnerComponent.strIn;
    }

    connect strIn -> innerComponent.strIn;
    connect innerComponent.strOut -> strOut;
}