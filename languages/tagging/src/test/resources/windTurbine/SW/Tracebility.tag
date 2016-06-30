package turbine;
conforms to nfp.TracebilityTagSchema;

tags Tracebility for Controller {
	tag PI_Controller with IsTraceable;
	tag Filtering with IsTraceable;
	tag MainController with IsTraceable;
	tag PitchEstimator with IsTraceable;
	tag BrakeController with IsTraceable;
	tag ParkController with IsTraceable;
	tag PitchRegulator with IsTraceable;
	
	/* syntactically sugar variant would look like:
	   tag with IsTraceable {
		  PI_Controller; Filtering; MainController; PitchEstimator; 
		  BrakeController; ParkController; PitchRegulator;
	   }
	*/
}