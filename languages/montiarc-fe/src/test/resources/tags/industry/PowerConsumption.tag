package industry;
conforms to nfp.PowerConsumptionTagSchema;

// in the example are all units in mW, but here I changed it for testing reasons
tags PowerConsumption for turbineController {
	tag piController with PowerConsumption = 150 mW;
	tag filtering with PowerConsumption = 40 mW;
	tag mainController with PowerConsumption = 50 W;
	tag pitchEstimator with PowerConsumption = 100 MW;
	tag brakeController with PowerConsumption = 20 kW;
	tag parkController with PowerConsumption = 20 mW;
	tag pitchRegulator with PowerConsumption = 120 mW;
	tag windSpeed with PowerTester = "MvW";
	tag parkController.out1 -> controlSignals with PowerBoolean = true;
	tag filtering with EnergyLabel = A++;
}