/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import com.google.common.base.Preconditions;

import java.util.ArrayList;

public class APIContext {
  // input expected to be functional
  private ArrayList<String> modelList;
  private ArrayList<String> symFiles;
  //private ArrayList<Path> hwc;

  // flags
  private boolean useC2MC;

  public APIContext(ArrayList<String> modelList, ArrayList<String> symFiles){
    Preconditions.checkNotNull(modelList);
    Preconditions.checkNotNull(symFiles);

    this.modelList = modelList;
    this.symFiles = symFiles;
  }

  public APIContext(ArrayList<String> modelList){
    Preconditions.checkNotNull(modelList);

    this.modelList = modelList;
    this.symFiles = new ArrayList<>();
  }

  public ArrayList<String> getModelList(){
    return this.modelList;
  }

  public ArrayList<String> getSymFiles(){
    return this.symFiles;
  }

}
