/* (c) https://github.com/MontiCore/monticore */
package factory.warehouse.transporter;

import java.util.ArrayList;
import factory.warehouse.StorageObject;
import factory.warehouse.Builder;
import factory.warehouse.StorageObjectLinkedList;
import java.lang.Integer;

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