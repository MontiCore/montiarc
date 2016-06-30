package industry3;
conforms to nfp.PowerConsumptionTagSchema;

// in the example are all units in mW, but here I changed it for testing reasons
tags PowerConsumption for turbineController {
	tag piController with PowerConsumption = 150;
	tag filtering with PowerConsumption = 40 s;
	tag mainController with PowerConsumption = 10 auto;
	tag pitchEstimator with PowerConsumption = 100 MW;
	tag brakeController with PowerConsumption;
	tag omega with PowerConsumption = 20 mW;
	tag mainController.pitchBrake -> brakeController.pitchBrake with PowerConsumption = 120 mW;
}