/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.oracle;

/** A mock class for controlling the hashCode method */
public class OptionMock {

  private final int hashCodeMock;

  public OptionMock(int hashCodeMock) {
    this.hashCodeMock = hashCodeMock;
  }

  @Override
  public String toString() {
    return "obj with hash: " + hashCodeMock;
  }

  @Override
  public int hashCode() {
    return hashCodeMock;
  }

  // Implementing equals to honor the contract between hashCode and equals
  // even if unnecessary
  @Override
  public boolean equals(Object obj) {
    return obj instanceof OptionMock && this.hashCode() == obj.hashCode();
  }
}
