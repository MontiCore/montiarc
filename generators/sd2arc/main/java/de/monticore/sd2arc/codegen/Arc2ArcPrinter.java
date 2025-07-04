/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc.codegen;

import de.monticore.prettyprint.IndentPrinter;
import montiarc._prettyprint.MontiArcFullPrettyPrinter;

public class Arc2ArcPrinter extends MontiArcFullPrettyPrinter {

  public Arc2ArcPrinter() {
    this(new IndentPrinter());
  }

  public Arc2ArcPrinter(IndentPrinter printer) {
    this(printer, true);
  }

  public Arc2ArcPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }
}
