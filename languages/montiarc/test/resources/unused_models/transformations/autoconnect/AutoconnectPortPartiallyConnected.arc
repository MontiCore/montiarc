/* (c) https://github.com/MontiCore/monticore */
package components.body.autoconnect;

import components.body.autoconnect.dummycomponents.DummyComponent5;

/**
 * Valid model.
 */
component AutoconnectPortPartiallyConnected {
    autoconnect port;

    port
        in String sIn,
        out String sOut;

    component DummyComponent5 e1, e2;

    connect sIn -> e1.sIn;
    connect e1.sOut -> e2.sIn;
}
