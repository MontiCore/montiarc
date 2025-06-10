/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.oracle;

/**
 * Entity that has an {@link Oracle}. Usually components will implement this.<p>
 * Note that this is an interface for internal processing and not intended
 * to be accessed in external interactions with the component.
 */
public interface OracleOwner {

  Oracle getOracle();
}
