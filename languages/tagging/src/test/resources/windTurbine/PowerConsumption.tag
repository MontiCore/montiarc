package industry;
conforms to nfp.PowerConsumptionTagSchema;

// in the example are all units in mW, but here I changed it for testing reasons
tags PowerConsumption for turbineController {
  tag parkController with ISBN = 978-3-86680-192-9; PowerTester = "MvW";
  tag nested with ComponentLayout =
      { id = 2, {pos = (10, 20), size = (15, 10), remark = "{"}, layoutPosition = 1,
        reservedHorizontalSpace = 10, isOnTop }; // isOnTop is true (b/c it is mentioned)

  tag MainController with IsTraceable    ;
  tag MainController with IsTraceable;
  tag filtering with ComponentLayout =
      { id = 2, pos = (10, 20), size = (15, 10), layoutPosition = 1,
        reservedHorizontalSpace = 10, isOnTop }; // isOnTop is true (b/c it is mentioned)
  tag piController with PowerConsumption=A++;
  tag pitchEstimator with xyz = { 200 A };
  tag windSpeed with PowerTester = "MvW";
  tag piController with PowerConsumption = A;
  tag piController with PowerConsumption = {150 mW};

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
  tag mainController with EnergyLabel = A3+;
  tag pitchEstimator with EmergencyNumber = 0170/455679;
}