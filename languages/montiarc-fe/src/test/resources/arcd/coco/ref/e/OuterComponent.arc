package e;

component OuterComponent {
        
    component InnerComponent extends OuterComponent {
    
    
    }
    
    component InnerComponent2 {
        
        component InnerInnerComponent extends OuterComponent {
        
        }
    }

}