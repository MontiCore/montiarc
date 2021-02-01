/* (c) https://github.com/MontiCore/monticore */
package factory.warehouse;

import factory.warehouse.StorageObject;
import factory.warehouse.Position;
import factory.warehouse.MovementState;
import factory.warehouse.CraneState
;

/**
 * A Crane moves StorageObjects from one position to another.
 * When holding an Object, horizontal movement is slow, therefore objects should be passed
 * to a transporter, which can only operate on ground level, but moves faster.
 */
component Crane {

  StorageObject currentlyHeld = null;

  port
    in Position sourcePosition,
    in Position targetPosition,
    in StorageObject storageObjectInput,
    out Position currentPosition,
    out CraneState craneState,
    out MovementState movementState,
    out StorageObject storageObjectOutput;
}