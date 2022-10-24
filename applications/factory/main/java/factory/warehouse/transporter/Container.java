/* (c) https://github.com/MontiCore/monticore */
package factory.warehouse.transporter;

public class Container extends ContainerTOP {
  
  public Container(int capacity) {
    super(capacity);
  }

  @Override
  public void init() {
    super.init();
    this.getStorageObjectOutput().setValue(null);
    this.getRemainingCapacity().setValue(capacity);
  }

  @Override
  public void compute() {
    super.compute();
    this.getStorageObjectOutput().setValue(null);
    this.getRemainingCapacity().setValue(capacity);
  }
}