/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

public class Signal extends SignalTOP {
  protected static Signal instance = new Signal();

  public static Signal get() {
    return instance;
  }
}
