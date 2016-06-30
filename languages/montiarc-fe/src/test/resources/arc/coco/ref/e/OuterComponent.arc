package e;

component OuterComponent {

    port
        in String;
        
    component InnerComponent extends OuterComponent {
        port
            in Integer;
    
    }
    
    component InnerComponent2 {
        port
            in Boolean;
            
        component InnerInnerComponent extends OuterComponent {
            port
                in Float;
        }
    }

}