package a;

component SuperArchitecturalComponent {
    
    port 
        in String myInput,
        out String myOutput,
        out String myOutput519;
        
   component A myA [strOut -> myOutput];
   
   connect myInput -> myA.strIn; 

}