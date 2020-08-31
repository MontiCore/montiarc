/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;


public class MontiArcLanguage extends MontiArcLanguageTOP {

  protected MontiArcModelLoader modelLoader;

  public MontiArcLanguage(String langName, String fileEnding) {
    super(langName, fileEnding);
    this.modelLoader = super.getModelLoader();
  }

  public static final String FILE_ENDING = "arc";

  public static final String LANGUAGE_NAME = "MontiArc";

  public MontiArcLanguage() {
    super(LANGUAGE_NAME, FILE_ENDING);
    this.modelLoader = super.getModelLoader();
  }

  @Override
  protected MontiArcModelLoader provideModelLoader() {
    return new MontiArcModelLoader(this);
  }

  @Override
  public String getSymbolFileExtension() {
    return null;
  }
}
