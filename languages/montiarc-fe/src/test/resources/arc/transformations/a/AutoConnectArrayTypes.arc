package a;

import testtypes.*;

component AutoConnectArrayTypes {
    
    autoconnect type;
    
    port 
        in String[] strIn,
        out String[] strOut;
    
    component ArrayComp ref;

    
    /* expected additional connectors
    strIn -> ref.strIn1;
    ref.strOut1 -> strOut;
    */
    
    /* forbidden additional connectors
    strIn -> ref.strIn2;
    ref.strOut2 -> strOut;
    */

}