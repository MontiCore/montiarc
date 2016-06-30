package industry;
conforms to nfp.TraceabilityTagSchema;

tags Traceability {
  tag turbineController.pitchEstimator with Traceable;
	// if components are not tagged with IsTraceable they are not traceable!
	tag MainController with IsTraceable;
	tag BrakeController with IsTraceable;
	tag PitchRegulator with IsTraceable;
}