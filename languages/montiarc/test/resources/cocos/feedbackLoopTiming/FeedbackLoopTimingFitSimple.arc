/* (c) https://github.com/MontiCore/monticore */
package feedbackLoopTiming;

/*
 * Valid model.
 */
component FeedbackLoopTimingFitSimple {
  component Inner {
    port in int iIn;
    port <<delayed>> out int iOut;
  }

  Inner inner1;
  inner1.iOut -> inner1.iIn;
}