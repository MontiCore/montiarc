/* (c) https://github.com/MontiCore/monticore */
package bumperbot;

import lejos.nxt.Button;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

/**
 * This is the handwritten equivalent to the auto generated bumperbot.
 * 
 * @author Gerrit Leonhardt
 */
public class BumperbotHW {
  final static int CYCLE_TIME = 10; // in ms
  
  public static void main(String[] args) {
    final BumperbotHW cmp = new BumperbotHW();
    long time;
    
    cmp.init();
    long a;
    while (!Thread.interrupted() && Button.readButtons() == 0) {
      time = System.currentTimeMillis();
      a= System.nanoTime();
      cmp.compute();
      System.out.println(System.nanoTime() - a);
      while ((System.currentTimeMillis() - time) < CYCLE_TIME) {
        Thread.yield();
      }
    }
  }
  
  private static enum State {
    Turning, Driving, Idle, Backing;
  }
  
  private final static int SPEED = 30;
  private final static int DELAY = 1000;
  
  private static final UltrasonicSensor sensor = new UltrasonicSensor(SensorPort.S1);
  private static final NXTMotor motorLeft = new NXTMotor(lejos.nxt.MotorPort.A);
  private static final NXTMotor motorRight = new NXTMotor(lejos.nxt.MotorPort.B);
  
  private State currentState;
  private long deadline;
  private boolean isRunning;
  
  private void init() {
    motorLeft.stop();
    motorRight.stop();
    currentState = State.Idle;
  }
  
  private void compute() {
    switch (currentState) {
      case Turning:
        // transition: Turning -> Driving
        if (isRunning && System.currentTimeMillis() >= deadline) {
          isRunning = false;
          // reaction
          motorLeft.forward();
          motorRight.forward();
          System.out.println("Driving");
          
          // state change
          currentState = State.Driving;
          break;
        }
        break;
      case Driving:
        // transition: Driving -> Backing
        if (sensor.getDistance() < 5) {
          // reaction
          motorLeft.backward();
          motorRight.backward();
          deadline = System.currentTimeMillis() + DELAY;
          isRunning = true;
          System.out.println("Backing");
          
          // state change
          currentState = State.Backing;
          break;
        }
        break;
      case Idle:
        // transition: Idle -> Driving
        // reaction
        motorLeft.forward();
        motorRight.forward();
        motorLeft.setPower(SPEED);
        motorRight.setPower(SPEED);
        System.out.println("Driving");
        
        // state change
        currentState = State.Driving;
        break;
      case Backing:
        // transition: Backing -> Turning
        if (isRunning && System.currentTimeMillis() >= deadline) {
          isRunning = false;
          // reaction
          motorLeft.forward();
          motorRight.backward();
          deadline = System.currentTimeMillis() + (DELAY << 1);
          isRunning = true;
          System.out.println("Truning");
          
          // state change
          currentState = State.Turning;
          break;
        }
        break;
    }
  }
}
