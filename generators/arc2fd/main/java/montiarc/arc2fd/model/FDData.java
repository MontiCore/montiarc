/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.model;

import java.util.List;
import java.util.Map;

/**
 * <code>FDData</code> is a container for the central information about a
 * feature diagram, such as the and-, or- and xor-groups and cross-tree requires
 * and excludes relations.
 *
 * @param root      Name of the root feature, i.e., the component
 * @param andGroups And-groups, by parent feature
 * @param groups    Or- and xor-groups
 * @param requires  Cross-tree requires relations
 * @param excludes  Cross-tree excludes relations
 */
public record FDData(String root,
                     Map<String, List<AndFeature>> andGroups,
                     List<FeatureGroup> groups,
                     List<FeaturePair> requires,
                     List<FeaturePair> excludes) { }
