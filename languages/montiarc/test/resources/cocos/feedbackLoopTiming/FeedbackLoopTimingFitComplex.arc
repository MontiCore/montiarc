/* (c) https://github.com/MontiCore/monticore */
package feedbackLoopTiming;

/*
 * Valid model.
 */
component FeedbackLoopTimingFitComplex {
  component InnerWithDelay {
    port in int iIn;
    port out int iOut; // This port is transitively delayed

    component Inner {
      port in int iIn;
      port <<delayed>> out int iOut; // This delay propagates out
    }

    Inner inner1;
    iIn -> inner1.iIn;
    inner1.iOut -> iOut;
  }

  component InnerWithoutDelay {
    port in int iIn;
    port out int iOut;
  }

  InnerWithDelay inner1;
  InnerWithoutDelay inner2;
  inner1.iOut -> inner2.iIn;
  inner2.iOut -> inner1.iIn; // Valid feedback loop with delay
}
