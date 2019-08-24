/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;

import components.body.subcomponents._subcomponents.HasStringInputAndOutput;

/**
*  Valid component. Inherits ports stringIn and stringOut from CorrectComp
*/
component ExistingPortInConnectorFromSuperComponent extends HasStringInputAndOutput {
    
    port
        out String stringOut2;
        
    component Inner {
        port
            in String sIn,
            out String sOut;
    }
    
    
    connect pIn -> inner.sIn;
    connect inner.sOut -> stringOut2, pOut;

}
