package turbine;
conforms to nfp.TimeTagSchema;

tags Time for Controller {
	tag PI_Controller with TestingLoadTime = 5 days;
	tag Filtering with TestingLoadTime = 4 days;
	tag MainController with TestingLoadTime = 17 days;
	tag PitchEstimator with TestingLoadTime = 6 days;
	tag BrakeController with TestingLoadTime = 3 days;
	tag ParkController with TestingLoadTime = 5 days;
	tag PitchRegulator with TestingLoadTime = 7 days;
	/* need syntactical sugar to tag an excel row:
	   tag with TestingLoadTime {
	      PI_Controller  =  5 days;
		  Filtering      =  4 days;
		  MainController = 17 days; ...
	   }
	*/
	
	tag PI_Controller with ExecutionTime = 600 microseconds; 
	tag Filtering with ExecutionTime = 250 us; // monticore does not support UTF-8 like µ
	tag MainController with ExecutionTime = 250 microseconds;
	tag PitchEstimator with ExecutionTime = 250 microseconds;
	tag BrakeController with ExecutionTime = 125 microseconds;
	tag ParkController with ExecutionTime = 125 microsecond;
	tag PitchRegulator with ExecutionTime = 125 microsecond;
}