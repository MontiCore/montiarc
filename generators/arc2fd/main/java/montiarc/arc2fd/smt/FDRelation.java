/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.smt;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Stores a Feature-Diagram Relation Type (i.e., could store all "XOR", or
 * all "AND" or all "OR")
 * For this, we maintain a Hash-Map where the key represents the left side of
 * the relation
 * and the value represents (a set) of right side equations.
 * <p>
 * By this, we can easily keep track of all relevant relations for the
 * feature diagram and this also allows
 * us to have subtrees.
 * <p>
 * For example, if we have "X" as a key in the hashmap (= left side), and
 * ["Y", "Z"] as a Value-Set ( = right side)
 * of the relation, this would mean that we have the relations "X -> Y" and
 * "X -> Z".
 */
public class FDRelation<T> {
  /**
   * Constant indicating a "Epsilon-Relation" (= Helper for correctly
   * converting formulas)
   */
  public static String EPSILON_RELATION = "epsilon_";
  /**
   * Constant indicating that the relation should start at the root (if used
   * as key)
   */
  public T root;
  /**
   * Typically, the key is the formula, but for "start at root", the key is
   * START_AT_ROOT
   */
  private HashMap<T, Set<T>> relations = new HashMap<>();

  /**
   * Creates a FDRelation with the given root.
   *
   * @param root Root of the FDRelation
   */
  public FDRelation(@NotNull T root) {
    Preconditions.checkNotNull(root);
    this.root = root;
    relations = new HashMap<>();
  }

  /**
   * Creates a FDRelation with the given root, and the left and right side of
   * the relation.
   *
   * @param root      Root of the FDRelation
   * @param leftSide  Left Side of the Relation
   * @param rightSide Right Side of the Relation
   */
  public FDRelation(@NotNull T root, @NotNull T leftSide,
                    @NotNull T rightSide) {
    Preconditions.checkNotNull(root);
    Preconditions.checkNotNull(leftSide);
    Preconditions.checkNotNull(rightSide);
    this.root = root;
    addRelation(leftSide, rightSide);
  }

  /**
   * Creates a FDRelation with the given root, a left relation and a Set
   * (multiple) right sides
   *
   * @param root      Root of the FDRelation
   * @param leftSide  Left Side of the Relation
   * @param rightSide Set of Right Sides for the Relation
   */
  public FDRelation(@NotNull T root, @NotNull T leftSide,
                    @NotNull Set<T> rightSide) {
    Preconditions.checkNotNull(root);
    Preconditions.checkNotNull(leftSide);
    Preconditions.checkNotNull(rightSide);
    this.root = root;
    addRelations(leftSide, rightSide);
  }

  /**
   * Creates a FDRelation with the given root and a right Side. The left side
   * is equivalent to the root.
   * So this creates a relation between the root and the given right side.
   *
   * @param root      Root of the FDRelation
   * @param rightSide Right Side (Relation from root to the right side)
   */
  public FDRelation(@NotNull T root, @NotNull T rightSide) {
    Preconditions.checkNotNull(root);
    Preconditions.checkNotNull(rightSide);
    // If we use this constructor, we want a relation from the root!
    this.root = root;
    addRootRelation(rightSide);
  }

  /**
   * Constructs and returns an Epsilon String (= Placeholder) with the given
   * version number
   *
   * @param version Version Number which should be used in the construction
   * @return Epsilon-String with Version Number
   */
  public static String getEpsilon(int version) {
    return EPSILON_RELATION + version;
  }

  /**
   * @return The Hash-Map
   */
  public HashMap<T, Set<T>> getRelationsHashMap() {
    return relations;
  }

  /**
   * Gets the Entries of the Relation Hash-Map by Key
   *
   * @param key Key we want to look for
   * @return Set stored in the Hash-Map for the given key
   */
  public Set<T> getRelationByKey(@NotNull T key) {
    Preconditions.checkNotNull(key);
    return relations.get(key);
  }

  /**
   * Adds a relation to the Hash-Map
   *
   * @param key      Key under which the relation should be stored
   * @param relation Value which should be stored
   */
  public void addRelation(@NotNull T key, @NotNull T relation) {
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(relation);
    addRelations(key, Set.of(relation));
  }

  /**
   * Adds a Set of Relations to the Hash-Map
   *
   * @param key          Key under which the relations should be stored
   * @param newRelations Set of Values which should be stored
   */
  public void addRelations(@NotNull T key, @NotNull Set<T> newRelations) {
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(newRelations);

    // If we have no entry in our Hashmap so far, we want to add it
    relations.putIfAbsent(key, new HashSet<>());

    // Add the relation by merging with existing values
    Set<T> combinedRelations = relations.get(key);
    combinedRelations.addAll(newRelations);
    relations.put(key, combinedRelations);
  }

  /**
   * Adds a relation to the Hash-Map (Key = left side of the Relation = Root)
   *
   * @param rightSide Value which should be stored
   */
  public void addRootRelation(@NotNull T rightSide) {
    Preconditions.checkNotNull(rightSide);
    addRelation(root, rightSide);
  }

  /**
   * Adds a Set of Relations to the Hash-Map (Key = left side of the Relation
   * = Root)
   *
   * @param rightSide Set of Values which should be stored
   */
  public void addRootRelations(@NotNull Set<T> rightSide) {
    Preconditions.checkNotNull(rightSide);
    addRelations(root, rightSide);
  }

  /**
   * Removes a given Object from the Root
   *
   * @param relationToRemove Object to remove
   */
  public void removeRootRelation(@NotNull T relationToRemove) {
    Preconditions.checkNotNull(relationToRemove);
    removeRelation(root, relationToRemove);
  }

  /**
   * Removes a set of given Object from the Root
   *
   * @param relationsToRemove Set of Objects to remove
   */
  public void removeRootRelations(@NotNull Set<T> relationsToRemove) {
    Preconditions.checkNotNull(relationsToRemove);
    removeRelations(root, relationsToRemove);
  }

  /**
   * Removes a given Object if it is present in the Hash-Map Entry of the
   * given key
   *
   * @param key              Key of the Hash-Map, where the Object should be
   *                         removed from
   * @param relationToRemove Object to remove
   */
  public void removeRelation(@NotNull T key, @NotNull T relationToRemove) {
    Preconditions.checkNotNull(relationToRemove);

    removeRelations(key, Set.of(relationToRemove));
  }

  /**
   * Removes a given Set of Objects if it is present in the Hash-Map Entry of
   * the given key
   *
   * @param key               Key of the Hash-Map, where the Objects should
   *                          be removed from
   * @param relationsToRemove Set of Objects to remove
   */
  public void removeRelations(@NotNull T key,
                              @NotNull Set<T> relationsToRemove) {
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(relationsToRemove);

    if (!relations.containsKey(key))
      return;
    Set<T> reducedRelations = relations.get(key);
    reducedRelations.removeAll(relationsToRemove);
    relations.put(key, reducedRelations);
  }

  /**
   * Removes a given Set of Objects if it is present in the Hash-Map Entry of
   * the given key
   *
   * @param keys Keys of the Hash-Map which should be dropped!
   */
  public void dropKeys(@NotNull Set<T> keys) {
    Preconditions.checkNotNull(keys);
    keys.forEach(k -> relations.remove(k));
  }

  /**
   * Adds the Map of the passed Relation onto the current relation
   *
   * @param newRelation Relation whose map should be added on the current
   *                    relation instance
   */
  public void addRelation(@NotNull FDRelation<T> newRelation) {
    Preconditions.checkNotNull(newRelation);
    newRelation.getRelationsHashMap().forEach(this::addRelations);
  }

  /**
   * Returns a Deep Copy of the current relation instance
   *
   * @return Deep Copy of the current instance
   */
  public FDRelation<T> getDeepCopy() {
    FDRelation<T> copy = new FDRelation<>(this.root);
    for (Map.Entry<T, Set<T>> entry : relations.entrySet()) {
      copy.addRelations(entry.getKey(), new HashSet<>(entry.getValue()));
    }
    return copy;
  }

  /**
   * Overwrite toString method for a nice output
   *
   * @return String-Variant of the HashMap
   */
  @Override
  public String toString() {
    return this.getRelationsHashMap().entrySet()
        .stream()
        .map(e -> e.getKey() + "=\"" + e.getValue() + "\"")
        .collect(Collectors.joining(", "));
  }
}
