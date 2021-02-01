/* (c) https://github.com/MontiCore/monticore */
package factory.warehouse;

import factory.warehouse.Storage;
import factory.warehouse.*;
import factory.warehouse.transporter.Transporter;
import factory.warehouse.Crane;

/**
 * A RequestManager is required to process StorageRequests and to coordinate a crane and a transporter.
 */
component RequestManager {//(Storage storage, Position dropOffZone, Position pickUpZone) {

  port
    in StorageRequest storageRequest,

    //ports for transporter communication
    in Position currentTransporterPosition,
    in Integer remainingTransporterCapacity,
    in MovementState transporterState,
    out Position transporterSource,
    out Position transporterTarget,

    //ports for crane communication
    in Position currentCranePosition,
    in CraneState craneState,
    in MovementState craneMovementState,
    out Position craneSource,
    out Position craneTarget,

    //ports for transporter-crane-exchange
    in StorageObject transporterStorageObjectOutput,
    in StorageObject craneStorageObjectOutput,
    out StorageObject transporterStorageObjectInput,
    out StorageObject craneStorageObjectInput;
}