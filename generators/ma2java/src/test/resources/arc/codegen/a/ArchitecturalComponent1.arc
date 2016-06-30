package a;

component ArchitecturalComponent1 {
    
    port 
        in String optimizedIn,
        in String normalIn,
        out String output1,
        out String output2,
        out String output3;
        
    component c.Generic<String> gen1, gen2, gen3, gen4;
    
    connect optimizedIn -> gen1.input;
    connect gen1.output -> output1;
    
    
    connect normalIn -> gen2.input, gen3.input;
    connect gen2.output -> output2;
    
    connect gen3.output -> gen4.input;
    connect gen4.output -> output3;
    
}