/* (c) https://github.com/MontiCore/monticore */
package components.body.timing;

import components.body.subcomponents._subcomponents.HasStringInputAndOutput;

/*
 * Valid model.
 *
 * Formerly 'UsingAandB'
 */
component UsingCompsWithTwoPortsAndWithAndWithoutDelay {
    port
        in String sIn,
        out String sOut;
        
    component HasStringInputAndOutput a;
    
    component DelayedComponentWithTwoPorts b;
    
    connect sIn -> b.sIn;
    connect b.sOut -> a.pIn;
    connect a.pOut -> sOut;

}
