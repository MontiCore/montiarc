package turbine;
conforms to nfp.PowerConsumptionTagSchema;

tags PowerConsumption for Controller {
	tag PI_Controller with PowerConsumption = 30 mW;
	tag Filtering with PowerConsumption = 50 mW;
	tag MainController with PowerConsumption = 30 mW;
	tag PitchEstimator with PowerConsumption = 70 mW;
	tag BrakeController with PowerConsumption = 10 mW;
	tag ParkController with PowerConsumption = 10 mW;
	tag PitchRegulator with PowerConsumption = 50 mW;
}