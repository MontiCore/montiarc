/* (c) https://github.com/MontiCore/monticore */
component MAStreamHelperTest {
    // Features
    feature a, b, c, d, e;

    // Constraints
    constraint (a && b);
    constraint (a || (b && c));

    // If-Else Statements
    if (a) {
        feature x;
    } else {
        feature y;
    }

    if (b) {}

    // Test Inner Component
    component innerComp1 { }
    component innerComp2 {
        feature z;
    }

    // Component Instance
    SubComponentTest subCompTest;
}
