/* (c) https://github.com/MontiCore/monticore */
package feedbackLoopTiming;

/*
 * Invalid model.
 */
component FeedbackLoopTimingMismatchSimple {
  component Inner {
    port in int iIn;
    port out int iOut;
  }

  Inner inner1;
  inner1.iOut -> inner1.iIn;
}