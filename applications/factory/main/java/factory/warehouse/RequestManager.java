/* (c) https://github.com/MontiCore/monticore */
package factory.warehouse;

public class RequestManager extends RequestManagerTOP {
  
  protected final static Position start = new Position();
  
  public RequestManager(Storage storage, Position dropOffZone, Position pickUpZone) {
    super(storage, dropOffZone, pickUpZone);
  }

  @Override
  public void init() {
    super.init();
    this.getTransporterSource().setValue(start);
    this.getTransporterTarget().setValue(start);
    this.getCraneSource().setValue(start);
    this.getCraneTarget().setValue(start);
    this.getTransporterStorageObjectInput().setValue(null);
    this.getCraneStorageObjectInput().setValue(null);
  }

  @Override
  public void compute() {
    super.compute();
    this.getTransporterSource().setValue(start);
    this.getTransporterTarget().setValue(start);
    this.getCraneSource().setValue(start);
    this.getCraneTarget().setValue(start);
    this.getTransporterStorageObjectInput().setValue(null);
    this.getCraneStorageObjectInput().setValue(null);
  }
}