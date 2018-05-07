package factoryTest.gen.factories;

public class ArchFactory {
  private static ArchFactory theInstance;

  private ArchFactory() {
  }

  public static ArchFactory getInstance() {
    if (theInstance == null) {
      theInstance = new ArchFactory();
    }
    return theInstance;
  }

  public factoryTest.gen.interfaces.IArch create() {
    return new factoryTest.gen.Arch();
  }
}