/* (c) https://github.com/MontiCore/monticore */
package factory.warehouse.transporter;

import java.util.ArrayList;
import factory.warehouse.Warehouse.StorageObject;
import factory.warehouse.Warehouse.Builder;
import factory.warehouse.Warehouse.StorageObjectLinkedList;

/**
 * Atomic component for holding StorageObjects.
 */
component Container(Integer capacity) {

  StorageObjectLinkedList container = Builder.buildStorageObjectLinkedList();

  port
    in StorageObject storageObjectInput,
    out StorageObject storageObjectOutput,
    out Integer remainingCapacity;
}