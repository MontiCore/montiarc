/* (c) https://github.com/MontiCore/monticore */
package arcucd._ast;

public class ASTArcUCDiagram extends ASTArcUCDiagramTOP {

  protected Boolean isDelayed;

  @Override
  public boolean isDelayed() {
    if (this.isDelayed != null) {
      return this.isDelayed;
    } else if (this.isPresentStereotype()) {
      this.isDelayed = this.getStereotype().contains(DELAY);
      return this.isDelayed;
    } else {
      this.isDelayed = false;
      return this.isDelayed;
    }
  }

}
