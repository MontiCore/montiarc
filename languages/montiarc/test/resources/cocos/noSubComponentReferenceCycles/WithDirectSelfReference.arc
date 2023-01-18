/* (c) https://github.com/MontiCore/monticore */
package noSubComponentReferenceCycles;

/**
 * Invalid model.
 */
component WithDirectSelfReference {
  WithDirectSelfReference selfRef;
}
