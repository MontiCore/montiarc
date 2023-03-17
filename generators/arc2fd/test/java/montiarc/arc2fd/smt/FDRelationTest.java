/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.smt;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FDRelationTest {
  String root = "root";
  String a = "a", b = "b", c = "c";
  FDRelation<String> relation = new FDRelation<>(root);

  /**
   * Method under test {@link FDRelation#getRelationsHashMap()}
   */
  @Test
  public void getRelationsHashMap() {
    // Given
    relation.addRelation(a, b);
    relation.addRootRelation(c);
    Map<String, Set<String>> expectedMap = new HashMap<>();
    expectedMap.put(a, Set.of(b));
    expectedMap.put(root, Set.of(c));

    // When && Then
    Assertions.assertNull(relation.getRelationByKey(c));
    Assertions.assertEquals(expectedMap, relation.getRelationsHashMap());
  }


  /**
   * Method under test {@link FDRelation#getRelationByKey(Object)}
   */
  @Test
  public void getRelationByKey() {
    // Given
    relation.addRelation(a, b);
    relation.addRootRelation(b);
    relation.addRootRelation(c);
    Set<String> expectedRelations = Set.of(b);

    // When && Then
    Assertions.assertNull(relation.getRelationByKey(c));
    Assertions.assertEquals(expectedRelations, relation.getRelationByKey(a));
  }


  /**
   * Method under test {@link FDRelation#addRelations(Object, Set)}
   */
  @Test
  public void addRelations() {
    // Given
    relation.addRelations(a, Set.of(b));
    relation.addRelations(root, Set.of(b, c));
    Set<String> expectedRelationsB = Set.of(b);
    Set<String> expectedRelationsC = Set.of(b, c);
    Map<String, Set<String>> expectedMap = new HashMap<>();
    expectedMap.put(a, expectedRelationsB);
    expectedMap.put(root, expectedRelationsC);

    // When && Then
    Assertions.assertNull(relation.getRelationByKey(c));
    Assertions.assertEquals(expectedRelationsB, relation.getRelationByKey(a));
    Assertions.assertEquals(expectedRelationsC,
        relation.getRelationByKey(root));
    Assertions.assertEquals(expectedMap, relation.getRelationsHashMap());
  }


  /**
   * Method under test {@link FDRelation#addRootRelations(Set)}}
   */
  @Test
  public void addRootRelations() {
    // Given
    Set<String> expectedSet = Set.of(a, b, c);
    relation.addRootRelations(expectedSet);
    Map<String, Set<String>> expectedMap = new HashMap<>();
    expectedMap.put(root, expectedSet);

    // When && Then
    Assertions.assertNull(relation.getRelationByKey(a));
    Assertions.assertEquals(expectedSet, relation.getRelationByKey(root));
    Assertions.assertEquals(expectedMap, relation.getRelationsHashMap());
  }


  /**
   * Method under test {@link FDRelation#addRelation(Object, Object)}
   */
  @Test
  public void addRelation() {
    // Given
    relation.addRelation(a, b);
    relation.addRelation(root, c);
    Set<String> expectedRelationsB = Set.of(b);
    Set<String> expectedRelationsC = Set.of(c);
    Map<String, Set<String>> expectedMap = new HashMap<>();
    expectedMap.put(a, expectedRelationsB);
    expectedMap.put(root, expectedRelationsC);

    // When && Then
    Assertions.assertNull(relation.getRelationByKey(c));
    Assertions.assertEquals(expectedRelationsB, relation.getRelationByKey(a));
    Assertions.assertEquals(expectedRelationsC,
        relation.getRelationByKey(root));
    Assertions.assertEquals(expectedMap, relation.getRelationsHashMap());
  }


  /**
   * Method under test {@link FDRelation#getDeepCopy()}
   */
  @Test
  public void getDeepCopy() {
    // Given
    relation.addRelation(a, b);
    relation.addRelation(root, c);

    // When
    FDRelation<String> newRelation = relation.getDeepCopy();
    relation = null;

    // Then
    Assertions.assertNotEquals(newRelation, relation);
    Assertions.assertNotNull(newRelation);
    Assertions.assertNull(relation);
  }
}
