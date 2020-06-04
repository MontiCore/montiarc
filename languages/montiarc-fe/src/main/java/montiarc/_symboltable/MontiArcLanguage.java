/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;


public class MontiArcLanguage extends MontiArcLanguageTOP {

  protected MontiArcModelLoader modelLoader = new MontiArcModelLoader(this);

  public MontiArcLanguage(String langName, String fileEnding) {
    super(langName, fileEnding);
  }

  public MontiArcLanguage() {
    super("MontiArc", ".mc4");
  }

  @Override
  protected MontiArcModelLoader provideModelLoader() {
    return this.modelLoader;
  }

  @Override
  public String getSymbolFileExtension() {
    return null;
  }
}
