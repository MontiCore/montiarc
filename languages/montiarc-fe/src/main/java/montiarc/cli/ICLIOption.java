/* (c) https://github.com/MontiCore/monticore */
package montiarc.cli;

public interface ICLIOption {

  /**
   * @return the name of this cli option.
   */
  String getName();

  /**
   *
   * @return the alternative long name of this cli option.
   */
  String getLongName();

  /**
   * @return the name of the cli option prefixed by a hyphen.
   */
  String printOption();

  /**
   * @return the long name of the cli option prefixed by two hyphens.
   */
  String printLongOption();
}