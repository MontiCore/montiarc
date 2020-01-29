/* (c) https://github.com/MontiCore/monticore */
package components.body.autoinstantiate;

import java.util.List;
import components.body.ports.Ports;

/*
 * Invalid model. Missing connectors.
 * TODO Fix test
 */
component Connectors {

    port 
        in String myInputString,
        out String stringOut,
        out List listOut,
        out List listOut2;
    
    component SomeComp sc;
    component Ports p;
    component ListInOut;
    
    // p2.p2 -> listOut, listInOut.lIn
    component Ports p2 [p2 -> listOut, listInOut];
    
    // p.p2 -> sc.myListIn
    connect p -> sc;
    
    // myInputString -> p.p1, sc.myIn, p2.p1;
    connect myInputString -> p, sc, p2;
    
    // sc.myOut -> stringOut
    connect sc -> stringOut;
    
    // listInOut.lOut -> listOut2
    connect listInOut -> listOut2;
    
    component InnerComponent {
        port 
            in String s1,
            in String s2;
    }
    
    // myInputString -> innerComponent.s1, innerComponent.s2;
    connect myInputString -> innerComponent;
    
    component Inner1 {
        port 
            in Integer i1,
            in Integer i2,
            in String s1In,
            in String s2In,
            in Boolean bool;
    }
    
    component Inner2 {
        port 
            in Double dIn,
            out Integer i3,
            out String s1Out,
            out String s2Out,
            out Boolean bool;
    }
    
    // inner2.i3 -> inner1.i1, inner1.i2
    // inner2.bool -> inner1.bool;
    connect inner2 -> inner1;
    
    component Inner3 {
        port 
            in String i3s1,
            in String i3s2;
    }
    
    component Inner4 {
        port
            in Number bIn,
            out String s4out;
    }
    
    component Inner4 
        // myInner4.s4out -> inner3.i3s1, inner3.i3s2
        myInner4 [s4out -> inner3];
}