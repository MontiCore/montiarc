package a;

import b.SubComponent1;
import c.*;
import java.util.List;

component Refs {
    port 
        in String strIn,
        in Integer intIn,
        in MyType typeIn,
        in List<List<String>> listListStringIn,
        out List listOut,
        out Integer intOut,
        out MyType typeOut,
        out List<List<String>> listListStringOut,
        out String sOut;
        
    component Ports myPortReference;
    
    component SubComponent1 mySubComponent1;
    
    component SubA mySubA;
    
    component SuperArchitecturalComponent;
    
    component Generic<List<List<String>>> g1;
    
    component Generic<MyType> g2;
    
    connect strIn -> myPortReference.p1, mySubComponent1.strIn, superArchitecturalComponent.myInput;
    connect superArchitecturalComponent.myOutput -> sOut;
    connect myPortReference.p2 -> listOut;
    connect intIn -> mySubA.anotherIn;
    connect mySubA.anotherOut -> intOut;
    connect listListStringIn -> g1.input;
    connect g1.output -> listListStringOut;
    connect typeIn -> g2.input;
    connect g2.output -> typeOut;
}