/* (c) https://github.com/MontiCore/monticore */
package localvardeclarations;

/*
 * Valid model.
 */
component UsesLocalVarCorrectly {
  port out int pOut;
  int compVar = 0;

  automaton {

    initial {
      int a;
      a = 15;
      int b = a;
      compVar = b;
      { // let's also try deeper nesting .1)
        int c = b;
        pOut = c;
      }
    } state A;

    A -> A / {
      int a;
      a = 15;
      int b = a;
      compVar = b;
      { // let's also try deeper nesting .2)
        int c = b;
        pOut = c;
      }
    };
  }
}
