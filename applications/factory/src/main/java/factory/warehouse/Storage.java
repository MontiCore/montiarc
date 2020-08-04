/* (c) https://github.com/MontiCore/monticore */
package factory.warehouse;

import factory.warehouse.Warehouse.Position;
import factory.warehouse.Warehouse.StorageObject;

public class Storage {
  
  private StorageObject[][][] storage;
  
  public Storage(int xDimension, int yDimension, int zDimension) {
    storage = new StorageObject[xDimension][yDimension][zDimension];
  }
  
  public StorageObject lookAt(Position pos) {
    return storage[pos.getPosX()][pos.getPosY()][pos.getPosZ()];
  }
  
  public StorageObject takeFrom(Position pos) {
    StorageObject ret = storage[pos.getPosX()][pos.getPosY()][pos.getPosZ()];
    storage[pos.getPosX()][pos.getPosY()][pos.getPosZ()] = null;
    return ret;
  }
  
  public boolean putAt(StorageObject storageObject, Position pos) {
    if(storage[pos.getPosX()][pos.getPosY()][pos.getPosZ()] != null) {
      return false;
    }
  
    storage[pos.getPosX()][pos.getPosY()][pos.getPosZ()] = storageObject;
    return true;
  }
}