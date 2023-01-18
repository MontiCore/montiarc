/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

import components.body.subcomponents.NotExistentToo;
import components.body.subcomponents._subcomponents.*;

/**
 * Invalid model.
 * @implements [Hab12] R3: Full qualified subcomponent types exist in the named package. (p. 28)
 * @implements [Hab12] R4: Unqualified subcomponent types either exist in the current package
 * or are imported using an import statement. (p. 28)
 */
component ReferencedSubComponentsNotExists {
  port
    in String s1,
    out String sout1,
    out String sout2,
    out String sout3;

  component Buffet;

  connect s1 -> buffer.input;
  connect buffer.output -> sout1;

  component NotExistent ne; //wrong: type does not exist
  component NotExistentToo; //wrong: type does not exist

  component HasStringInputAndOutput cc1;
  component components.body.subcomponents._subcomponents
              .HasStringInputAndOutput cc2;
  component HasStringInputAndOutput cc3;

  connect s1 -> cc1.pIn, cc2.pIn, cc3.pIn;
  connect cc1.pOut -> sout1;
  connect cc2.pOut -> sout2;
  connect cc3.pOut -> sout3;
}
