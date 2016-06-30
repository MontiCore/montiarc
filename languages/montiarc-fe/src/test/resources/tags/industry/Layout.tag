package industry;
conforms to nfp.LayoutTagSchema;

tags LayoutInformation for turbineController {

	tag piController with ComponentLayout =
	  { id = 2, pos = (10, 20), size = (15, 10), layoutPosition = 1,
	  reservedHorizontalSpace = 10 } ; // isOnTop is false (b/c it is not mentioned)

  tag filtering with ComponentLayout =
      { id = 7, pos = (20, 30), size = (50, 60), layoutPosition = 8,
      reservedHorizontalSpace = 50, isOnTop } ; // isOnTop is true (b/c it is mentioned)

  tag mainController with ComponentLayout =
      { id = 3, pos = (12, 28), size = (17, 30), layoutPosition = 100,
      reservedHorizontalSpace = 10 } ; // isOnTop is false (b/c it is not mentioned)

  tag pitchEstimator with ComponentLayout =
      { id = 6, pos = (25, 48), size = (17, 9), layoutPosition = 80,
      reservedHorizontalSpace = 99 } ; // isOnTop is false (b/c it is not mentioned)

  tag brakeController with ComponentLayout =
      { id = 800, pos = (700, 210), size = (185, 150), layoutPosition = 3,
      reservedHorizontalSpace = 10,isOnTop } ; // isOnTop is false (b/c it is not mentioned)

  tag parkController with ComponentLayout =
      { id = 459, pos = (120, 205), size = (158, 140), layoutPosition = 321,
      reservedHorizontalSpace = 89525 } ; // isOnTop is false (b/c it is not mentioned)

  tag parkController.out1 -> controlSignals with ConnectorLayout =
        { id = 80, pos = (30, 50), end = (80, 90), mid = (70, 75) } ;

  tag mainController.pitchBrake -> brakeController.pitchBrake with ConnectorLayout =
         { id = 54, pos = (65, 32), end = (5, 9), mid = (4, 2) } ;
   }