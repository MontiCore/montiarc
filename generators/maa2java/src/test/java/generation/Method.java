/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package generation;

import de.monticore.java.javadsl._ast.*;
import de.monticore.types.types._ast.ASTReturnType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTVoidType;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author (last commit) Michael Mutert
 * @version , 2018-06-22
 * @since TODO
 */
public class Method{
  private final ASTReturnType returnType;
  private final String name;
  private final ASTFormalParameters params;
  private List<String> bodyElements;

  public Method(ASTReturnType returnType, String name,
                ASTFormalParameters params, List<String> bodyElements) {
    this.returnType = returnType;
    this.name = name;
    this.params = params;
    this.bodyElements = bodyElements;
  }

  static Builder getBuilder(){
    return new Builder();
  }

  public ASTReturnType getReturnType() {
    return returnType;
  }

  public String getName() {
    return name;
  }

  public List<String> getBodyElements() {
    return bodyElements;
  }

  public ASTFormalParameters getParams() {
    return params;
  }

  public void addBodyElement(String element){
    this.bodyElements.add(element.replaceAll("\\s", ""));
  }

  public void addBodyElement(String element, int index) {
    final int size = this.bodyElements.size();
    if(size + index < 0){
      throw new IndexOutOfBoundsException();
    }
    if(index < 0){
      index = size + index;
    }
    this.bodyElements.add(index, element.replaceAll("\\s", ""));
  }

  static class Builder{

    private ASTReturnType returnType;
    private String name;
    private List<ASTFormalParameter> parameters;
    private List<String> bodyElements;

    public Builder() {
      this.bodyElements = new ArrayList<>();
      this.parameters = new ArrayList<>();
    }

    public Builder setReturnType(ASTReturnType returnType) {
      this.returnType = returnType;
      return this;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder addParameter(String identifier, ASTType type){
      final ASTDeclaratorId id = JavaDSLMill.declaratorIdBuilder()
                                     .setName(identifier).build();
      final ASTFormalParameter param = JavaDSLMill.formalParameterBuilder()
                                           .setType(type)
                                           .setDeclaratorId(id)
                                           .build();
      this.parameters.add(param);
      return this;
    }

    public Builder addParameters(List<ASTFormalParameter> parameters){
      this.parameters.addAll(parameters);
      return this;
    }

    public Builder addBodyElements(List<String> bodyElements) {
      for (String element : bodyElements) {
        addBodyElement(element);
      }
      return this;
    }

    public Builder addBodyElement(String element){
      this.bodyElements.add(element.replaceAll("\\s", ""));
      return this;
    }

    public Method build() {
      ASTFormalParameters formalParameters;
      if(this.parameters.isEmpty()){
        formalParameters = JavaDSLMill.formalParametersBuilder().build();
      } else{
        ASTFormalParameterListing listing =
            JavaDSLMill.formalParameterListingBuilder()
                .setFormalParameterList(this.parameters)
                .build();
        formalParameters = JavaDSLMill.formalParametersBuilder()
                               .setFormalParameterListing(listing)
                               .build();
      }
      if(returnType == null){
        returnType = GenerationConstants.VOID_TYPE;
      }
      return new Method(returnType, name, formalParameters, bodyElements);
    }
  }

  @Override public String toString() {
    return "Method{" +
        "returnType=" + returnType +
        ", name='" + name + '\'' +
        ", params=" + params +
        ", bodyElements=" + bodyElements +
        '}';
  }
}
