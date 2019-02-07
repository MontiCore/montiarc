/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package infrastructure;

import com.google.common.collect.Lists;
import de.monticore.java.javadsl._ast.*;
import de.monticore.types.types._ast.ASTType;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author (last commit) Michael Mutert
 * @version ,
 * @since TODO
 */
public class Constructor{

  public String getName() {
    return name;
  }

  public ASTFormalParameters getParameters() {
    return parameters;
  }

  public List<String> getBodyElements() {
    return bodyElements;
  }

  private final String name;
  private final ASTFormalParameters parameters;
  private List<String> bodyElements;

  private Constructor(String name,
                      ASTFormalParameters parameters,
                      List<String> bodyElements) {
    this.name = name;
    this.parameters = parameters;
    this.bodyElements = bodyElements;
  }

  public static Builder getBuilder(){
    return new Builder();
  }

  public static class Builder{

    public List<ASTFormalParameter> getParameters() {
      return parameters;
    }

    public List<String> getBodyElements() {
      return bodyElements;
    }

    private String name;
    private List<ASTFormalParameter> parameters;
    private List<String> bodyElements;

    Builder(){
      bodyElements = new ArrayList<>();
      parameters = new ArrayList<>();
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

    public Builder addBodyElement(String element){
      this.bodyElements.add(element.replaceAll("\\s", ""));
      return this;
    }

    public Builder addBodyElements(List<String> bodyElements){
      for (String element : bodyElements) {
        addBodyElement(element);
      }

      return this;
    }

    public Builder setName(String name){
      this.name = name;
      return this;
    }

    public Builder clearBodyElements(){
      this.bodyElements = new ArrayList<>();
      return this;
    }

    public Builder clearParameters(){
      this.parameters = new ArrayList<>();
      return this;
    }

    public Constructor build(){
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
      return new Constructor(name, formalParameters, Lists.newArrayList(bodyElements));
    }
  }

  @Override public String toString() {
    String parameterString = "[]";
    if(parameters.isPresentFormalParameterListing()){
      parameterString = GeneratorTestConstants.PRINTER.prettyprint(parameters.getFormalParameterListing());
    }

    return "Constructor{" +
        "name='" + name + '\'' +
        ", parameters=" + parameterString +
        ", bodyElements=" + bodyElements +
        '}';
  }
}
