/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.model;

import java.util.List;

/**
 * <code>FeatureGroup</code> stores a group of features belonging to a parent
 * feature and a boolean if the group is a xor-group.
 *
 * @param parent
 * @param features
 * @param xor
 */
public record FeatureGroup(String parent,
                           List<String> features,
                           boolean xor) { }
