/* (c) https://github.com/MontiCore/monticore */
package openmodeautomata.runtime;


import org.codehaus.commons.nullanalysis.NotNull;

public interface DataType {

  /**
   * @return true, if this type equals the given other type
   */
  default boolean equals(@NotNull DataType type) {
    return equalsOrExtends(type) && equalsOrGeneralizes(type);
  }

  /**
   * @return true, if this type extends the given other type (or equals it)
   */
  boolean equalsOrExtends(@NotNull DataType type);

  /**
   * @return true, if the given type extends this one (or equals it)
   */
  boolean equalsOrGeneralizes(@NotNull DataType type);

  /**
   * @return a simple name for this type
   */
  String getName();
}
