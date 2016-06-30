package industry;
conforms to nfp.TimeTagSchema;

tags Time for Controller {
	tag piController with TestingLoadTime = 5 days;
	tag filtering with TestingLoadTime = 4 days;
	tag mainController with TestingLoadTime = 17 days;
	tag pitchEstimator with TestingLoadTime = 6 days;
	tag brakeController with TestingLoadTime = 3 days;
	tag parkController with TestingLoadTime = 5 days;
	tag pitchRegulator with TestingLoadTime = 7 days;
	/* need syntactical sugar to tag an excel row:
	   tag with TestingLoadTime {
	      PI_Controller  =  5 days;
		  Filtering      =  4 days;
		  MainController = 17 days; ...
	   }
	*/
	
	tag piController with ExecutionTime = 600 microseconds;
	tag filtering with ExecutionTime = 250 us; // monticore does not support UTF-8 like µ
	tag mainController with ExecutionTime = 250 microseconds;
	tag pitchEstimator with ExecutionTime = 250 microseconds;
	tag brakeController with ExecutionTime = 125 microseconds;
	tag parkController with ExecutionTime = 125 microsecond;
	tag pitchRegulator with ExecutionTime = 125 microsecond;
}