package pretty;

component InnerComponents {
  
    component InnerSimpleComponent {
        
        component InnerInnerComponent {
        
        }
        
        component InnerInnerGenericComponent<K, V> {
        
        }
        
        component InnerInnerConfigurableComponent(int x, String z, U v) {
        
        }
        
        component InnerInnerGenericAndConfigurableComponent<K, V>(String s, int i) {
        
        }
    
    }
    
    component InnerGenericComponent<K, V> {
    
    }
    
    component InnerConfigurableComponent(int x, String z, U v) {
        
    }
        
    component InnerGenericAndConfigurableComponent<K, V>(String s, int i) {
        
    }
    
    component BlaBla myBla {
    
    }
  
  
  

}