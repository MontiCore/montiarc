/* (c) https://github.com/MontiCore/monticore */
package components.head.inheritance;

import components.body.subcomponents._subcomponents.HasStringInputAndOutput;

/**
 * Invalid model.
 * TODO Check Validity: Inheritance of subcomponents?
 */
component SubB extends SuperArchitecturalComponent {
    
    port 
        in String anotherIn,
        out String anotherOut,
        out String anotherOut2;    


    component HasStringInputAndOutput anotherA [pOut->anotherOut2];
    component HasStringInputAndOutput yetAnotherA ;
    
    connect myA.pOut -> anotherOut;
    connect anotherIn -> anotherA.pIn;
    
    connect yetAnotherA.pOut -> myOutput519;
}
