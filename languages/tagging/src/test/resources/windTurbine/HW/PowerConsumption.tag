package turbine;
conforms to nfp.TracebilityTagSchema;

tags PowerConsumption for Controller {
	tag PI_Controller with PowerConsumption = 150 mW;
	tag Filtering with PowerConsumption = 40 mW;
	tag MainController with PowerConsumption = 50 mW;
	tag PitchEstimator with PowerConsumption = 100 mW;
	tag BrakeController with PowerConsumption = 20 mW;
	tag ParkController with PowerConsumption = 20 mW;
	tag PitchRegulator with PowerConsumption = 120 mW;
}