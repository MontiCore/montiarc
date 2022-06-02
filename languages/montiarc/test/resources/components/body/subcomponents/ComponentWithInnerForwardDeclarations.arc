/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

/**
 * Valid model.
 */
component ComponentWithInnerForwardDeclarations {
  component DriverInterface driverInterface;
  component Powertrain powertrain;
  component Chassis chassis;

  //pedal connections
  connect driverInterface.throttle -> powertrain.throttleGasPedal;
  connect driverInterface.brake -> chassis.pedalBrake;
  connect chassis.currentSpeed -> driverInterface.currentSpeed;


  //subcomponents
  component DriverInterface {
    component InterfaceController controller;

    port out double throttle;
    port out double brake;
    port in double currentSpeed;

    //pedal connections
    connect gasPedal.percentage -> throttle;
    connect brakePedal.percentage -> brake;
    connect brakePedal.percentage -> controller.brake;

    //display connections
    connect controller.accIsEnabled -> display.accIsEnabled;
    connect controller.targetSpeed -> display.targetSpeed;
    connect currentSpeed -> display.currentSpeed;

    //acc information connections
    connect accActivator.turnOn -> controller.turnOn;
    connect accActivator.speedChange -> controller.speedChange;
    connect currentSpeed -> controller.currentSpeed;

    //subsubcomponents
    component InterfaceController {
      port in double brake;
      port in boolean turnOn;
      port in double speedChange;

      port in double currentSpeed;
      port out double targetSpeed;
      port out boolean accIsEnabled;
    }

    component Pedal{
      port out double percentage;
    }
    component Pedal gasPedal;
    component Pedal brakePedal;

    component AccTerminal{
      port out boolean turnOn;
      port out double speedChange;
    }
    component AccTerminal accActivator;

    component Display {
      port in boolean accIsEnabled;
      port in double targetSpeed;
      port in double currentSpeed;
    }
    component Display display;
  }

  component Powertrain {

    port in double throttleGasPedal;

    connect throttleGasPedal -> controller.throttleGasPedal;
    connect controller.engineThrottle -> engine.throttle;

    component Engine {
      port in double throttle;
    }
    component Engine engine;

    component PowerController{
      port in double throttleGasPedal;
      port out double engineThrottle;
    }
    component PowerController controller;
  }

  component Chassis {

    port in double pedalBrake;
    port out double currentSpeed;

    connect pedalBrake -> controller.pedalBrake;
    connect rotationSensor.currentSpeed -> currentSpeed;
    connect controller.force -> brakes.force;

    component Brakes {
      port in double force;
    }
    component Brakes brakes;

    component ChassisController{
      port in double pedalBrake;
      port out double force;
    }
    component ChassisController controller;

    component RotationSensor{
      port out double currentSpeed;
    }
    component RotationSensor rotationSensor;
  }
}
