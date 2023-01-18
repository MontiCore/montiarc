/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;

import components.body.subcomponents._subcomponents.HasStringInputAndOutput;

/*
 * Invalid model.
 * Unqualified source and target ports in connectors do not exist.
 *
 * @implements [Hab16] CO3: Unqualified sources or targets in connectors
 *                           either refer to a port or a subcomponent in
 *                           the same namespace. (p.61 Lst. 3.35)
 */
component MissingSourceTargetDefinition {

    port
        in String sIn,
        out String sOut,
        out String sOut2;

    component HasStringInputAndOutput cc;

    connect sIn -> ccWrong;  // No target port "ccWrong" in MissingSourceTargetDefinition

    connect sInWrong -> cc.pIn; // No input port "sInWrong" in MissingSourceTargetDefinition

    connect cc.pOut -> sOutWrong; // No target port "sOutWrong" in MisingSourceTargetDefinition

    connect ccWrong -> sOut; // No souce port "ccWrong" in component MissingSourceTargetDefinition

    // correct connectors
    connect sIn -> cc.pIn;
    connect cc.pOut -> sOut2;
}
