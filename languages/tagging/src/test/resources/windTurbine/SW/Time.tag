package turbine;
conforms to nfp.TimeTagSchema;

tags Time for Controller {
	tag PI_Controller with TestingLoadTime = 6 days;
	tag Filtering with TestingLoadTime = 3 days;
	tag Main_Controller with TestingLoadTime = 2 weeks;
	tag PitchEstimator with TestingLoadTime = 4 days;
	tag BrakeController with TestingLoadTime = 3 days;
	tag ParkController with TestingLoadTime = 6 days;
	tag PitchRegulator with TestingLoadTime = 6 days;
	
	tag PI_Controller with ExecutionTime = 600 microseconds;
	tag Filtering with TestingLoadTime = 110 nanoseconds;
	tag Main_Controller with TestingLoadTime = 250 microseconds;
	tag PitchEstimator with TestingLoadTime = 60 nanoseconds;
	tag BrakeController with TestingLoadTime = 250 microseconds;
	tag ParkController with TestingLoadTime = 1 microsecond;
	tag PitchRegulator with TestingLoadTime = 125 microsecond;
}