/* (c) https://github.com/MontiCore/monticore */
package factory.warehouse.transporter;

import java.util.ArrayList;
import factory.warehouse.StorageObject;

import java.lang.Integer;

/**
 * Atomic component for holding StorageObjects.
 */
component Container(Integer capacity) {

  //ArrayList <StorageObject> storedObjects = new ArrayList <StorageObject> ();

  port
    in StorageObject storageObjectInput,
    out StorageObject storageObjectOutput,
    out Integer remainingCapacity;
}