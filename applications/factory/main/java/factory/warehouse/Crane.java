/* (c) https://github.com/MontiCore/monticore */
package factory.warehouse;

public class Crane extends CraneTOP {
  
  protected final static Position start = new Position();

  public Crane() {
    super();
  }

  @Override
  public void init() {
    super.init();
    this.getCurrentPosition().setValue(start);
    this.getCraneState().setValue(CraneState.EMPTY);
    this.getMovementState().setValue(MovementState.WAITING);
    this.getStorageObjectOutput().setValue(null);
  }

  @Override
  public void compute() {
    super.compute();
    this.getCurrentPosition().setValue(start);
    this.getCraneState().setValue(CraneState.EMPTY);
    this.getMovementState().setValue(MovementState.WAITING);
    this.getStorageObjectOutput().setValue(null);
  }
}