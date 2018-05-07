package componentTest.gen.factories;

public class MergeFactory {

  private static MergeFactory theInstance;

  private MergeFactory() {
  }

  public static MergeFactory getInstance() {
    if (theInstance == null) {
      theInstance = new MergeFactory();
    }
    return theInstance;
  }

  public <T> componentTest.gen.interfaces.IMerge<T> create(String encoding,
      java.lang.Class<T> genericType) {
    return new componentTest.impl.Merge<T>(encoding, genericType);
  }
}