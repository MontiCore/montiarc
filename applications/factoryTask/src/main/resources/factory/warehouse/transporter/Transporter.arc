/* (c) https://github.com/MontiCore/monticore */
package factory.warehouse.transporter;

import factory.warehouse.StorageObject;
import factory.warehouse.MovementState;
import factory.warehouse.Position;

/**
 * A Transporter moves StorageObjects from one position to another, but can only operate on ground level.
 * To obtain a StorageObject, it has to be passed from a Crane.
 * To get rid of a StorageObject, it has to be taken by a crane.
 */
component Transporter(Integer capacity) {

  Container container(capacity);

  port
    in Position sourcePosition,
    in Position targetPosition,
    in StorageObject storageObjectInput,
    out Position currentPosition,
    out Integer remainingCapacity,
    out MovementState movementState,
    out StorageObject storageObjectOutput;

    storageObjectInput -> container.storageObjectInput;
    container.storageObjectOutput -> storageObjectOutput;
    container.remainingCapacity -> remainingCapacity;

}