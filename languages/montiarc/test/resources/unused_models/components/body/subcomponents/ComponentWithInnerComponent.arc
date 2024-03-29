/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

import components.body.subcomponents._subcomponents.AtomicComponent;

/*
 * Valid model.
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
            component SimpleReferencedComponent comp;

            port
                in String strIn,
                out String strOut;

            connect strIn -> comp.input;
            connect comp.output -> strOut;
        }

        connect innerInnerComponent.strOut -> strOut;
        connect strIn -> innerInnerComponent.strIn;
    }

    connect strIn -> innerComponent.strIn;
    connect innerComponent.strOut -> strOut;

}
