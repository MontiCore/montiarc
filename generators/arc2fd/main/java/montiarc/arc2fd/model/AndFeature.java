/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.model;

/**
 * <code>AndFeature</code> stores the name of a feature and whether it is optional
 * in its and-group.
 *
 * @param name
 * @param optional
 */
public record AndFeature(String name, boolean optional) { }
