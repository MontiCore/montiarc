package components.body.subcomponents;

/**
* Invalid model.
* An inner component must not extend its defining component.
* @Implements [Hab16] R12: An inner component type definition must not
* extend the component type in which it is defined. (p. 68, lst. 3.47)
*/
component OuterComponent {

    port
        in String str;

    component InnerComponent2 {
        port
            in Boolean b;

        component InnerInnerComponent extends OuterComponent { //invalid
            port
                in Float f;
        }
    }
    component InnerComponent extends OuterComponent { //invalid
        port
            in Integer integer;

    }

    component InnerComponent3 {
        port
            in Boolean b;

        component InnerInnerComponent extends InnerComponent3 { //invalid
            port
                in Float f;
        }
    }

}