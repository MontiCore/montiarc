/* (c) https://github.com/MontiCore/monticore */
package localvardeclarations;

/*
 * Invalid model. All usages of localBlock1, localBlock2, innerLocalBlock1, innerLocalBlock2 are illegal.
 */
component UsesLocalVarOutOfScope {
  port out int pOut, pOut2;
  int compVar1 = localBlock1;    // illegal
  int compVar2 = localBlock2;    // illegal

  automaton {

    initial {
      { int innerLocalBlock1 = 12; }
      int localBlock1;
      pOut2 = localBlock2;          // illegal

      pOut = innerLocalBlock1;      // illegal
    } state A;

    A -> A / {
      { int innerLocalBlock2 = 12; }
      int localBlock2;
      pOut2 = localBlock1;          // illegal

      pOut = innerLocalBlock2;      // illegal
    };
  }
}
