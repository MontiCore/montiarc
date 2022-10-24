/* (c) https://github.com/MontiCore/monticore */
package montiarc.lejos.lib.logger;

/**
 * Component behavior implementation for simple logging.
 * 
 */
public class Logger extends LoggerTOP {

  @Override
  public void compute() {
    if (this.getMessage().getValue() != null) {
      System.out.println(this.getMessage().getValue());
    }
  }

}
