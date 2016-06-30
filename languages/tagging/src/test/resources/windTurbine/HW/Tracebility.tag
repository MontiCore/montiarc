package turbine;
conforms to nfp.TracebilityTagSchema;

tags Tracebility for Controller {
	// if components are not tagged with IsTraceable they are not traceable!
	tag MainController with IsTraceable;
	tag BrakeController with IsTraceable;
	tag PitchRegulator with IsTraceable;
}