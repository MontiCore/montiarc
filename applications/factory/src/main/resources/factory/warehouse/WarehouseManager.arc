/* (c) https://github.com/MontiCore/monticore */
package factory.warehouse;

import factory.warehouse.RequestManager;
import factory.warehouse.transporter.Transporter;
import factory.warehouse.Crane;
import factory.warehouse.*;
import java.lang.Integer;

component WarehouseManager {

  Storage storage = Builder.buildStorage();
  Position dropOffZone = Builder.buildPosition();
  Position pickUpZone = Builder.buildPosition();

  RequestManager requestManager (storage, dropOffZone, pickUpZone);
  Transporter transporter (10);
  Crane crane;

  port in StorageRequest storageRequest;
  //transporter connections
  transporter.currentPosition -> requestManager.currentTransporterPosition;
  transporter.remainingCapacity -> requestManager.remainingTransporterCapacity;
  transporter.movementState -> requestManager.transporterState;
  requestManager.transporterSource -> transporter.sourcePosition;
  requestManager.transporterTarget -> transporter.targetPosition;

  //crane connections
  crane.currentPosition -> requestManager.currentCranePosition;
  crane.craneState -> requestManager.craneState;
  crane.movementState -> requestManager.craneMovementState;
  requestManager.craneSource -> crane.sourcePosition;
  requestManager.craneTarget -> crane.targetPosition;

  //transporter-crane-connections
  transporter.storageObjectOutput -> requestManager.transporterStorageObjectOutput;
  requestManager.transporterStorageObjectInput-> transporter.storageObjectInput;
  crane.storageObjectOutput -> requestManager.craneStorageObjectOutput;
  requestManager.craneStorageObjectInput -> crane.storageObjectInput;

  //manager request connection
  storageRequest -> requestManager.storageRequest;
}