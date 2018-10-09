package components.body.ajava;

/**
 * Invalid model. See comments below. 
 */
component DuplicateLocalVariables {
  int i;

  compute {
    int j = 1;
    
    if(true) {
      int k = 1;
    }
    
    if(true) {
      int i = 1; // Error: variable i already exists
    }
    
    for (int i=0; i<10; i++) { //Error: variable i already exists
    
    }
    
    for (int j=0; j<10; j++) { //Error: variable j already exists
      int i = 2; //Error: variable i already exists
    }
  }

}