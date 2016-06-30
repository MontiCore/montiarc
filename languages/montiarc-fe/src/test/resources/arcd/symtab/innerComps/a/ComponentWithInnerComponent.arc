package a;

component ComponentWithInnerComponent {

    port
        in String strIn,
        out String strOut;

    component Ref myReference;

    component InnerComponent {
    
        port
            in String strIn,
            out String strOut;

        component Ref myReference;
        
        component InnerInnerComponent {
            port
                in String strIn,
                out String strOut;

           component Ref myReference;
        }

        connect innerInnerComponent.strOut -> strOut; 
        connect strIn -> innerInnerComponent.strIn;
    }

    connect strIn -> innerComponent.strIn;
    connect innerComponent.strOut -> strOut;
}