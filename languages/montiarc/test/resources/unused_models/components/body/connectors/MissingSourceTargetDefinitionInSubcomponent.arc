/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;

import components.body.subcomponents._subcomponents.HasStringInputAndOutput;

/*
 * Invalid model.
 * As noted below, there are ports used in some connectors that do not exist.
 *
 * @implements [Hab16] CO3: Unqualified sources or targets in connectors
 *                           either refer to a port or a subcomponent in
 *                           the same namespace. (p.61 Lst. 3.35)
 * @implements [Hab16] R5: The first part of a qualified connector’s source
 *                          respectively target must correspond to a
 *                          subcomponent declared in the current component
 *                          definition. (p.64 Lst. 3.40)
 * @implements [Hab16] R6: The second part of a qualified connector’s source
 *                          respectively target must correspond to a port
 *                          name of the referenced subcomponent determined
 *                          by the first part. (p.64 Lst. 3.41)
 * @implements [Hab16] R7: The source port of a simple connector must exist
 *                          in the subcomponents type. (p.65 Lst. 3.42)
 */
component MissingSourceTargetDefinitionInSubcomponent {

    component Inner {
        port
            in String sInnerIn,
            out String sInnerOut;
    }

    port
        in String sIn,
        out String sOut,
        out String sOut2;

    component HasStringInputAndOutput cc;


    connect sIn -> inner.sInnerIn; // Correct

    connect sIn -> inner.sInnerInWrong; //Error: sInnerInWrong does not exist

    connect inner.sInnerOutWrong -> sOut; //incorrect

    connect inner.sInnerOut -> cc.pIn; //Error: stringIn does not exist

    connect cc.pOut -> sOut2; //correct
}
