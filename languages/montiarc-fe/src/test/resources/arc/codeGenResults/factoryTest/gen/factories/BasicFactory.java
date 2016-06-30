package factoryTest.gen.factories;

public class BasicFactory {
  private static BasicFactory theInstance;

  private BasicFactory() {
  }

  public static BasicFactory getInstance() {
    if (theInstance == null) {
      theInstance = new BasicFactory();
    }
    return theInstance;
  }

  public factoryTest.gen.interfaces.IBasic create() {
    return new factoryTest.impl.Basic();
  }
}