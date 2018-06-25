/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package generation;

import de.monticore.java.javadsl._ast.ASTDeclaratorId;
import de.monticore.java.javadsl._ast.ASTFormalParameter;
import de.monticore.java.javadsl._ast.ASTFormalParameterListing;
import de.monticore.java.javadsl._ast.ASTFormalParameters;
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

  static Builder getBuilder(){
    return new Builder();
  }

  static class Builder{

    private String name;
    private List<ASTFormalParameter> parameters;
    private List<String> bodyElements;

    Builder(){
      bodyElements = new ArrayList<>();
      parameters = new ArrayList<>();
    }

    public Builder addParameter(String identifier, ASTType type){
      final ASTDeclaratorId id = ASTDeclaratorId.getBuilder()
                                     .name(identifier).build();
      final ASTFormalParameter param = ASTFormalParameter
                                           .getBuilder()
                                           .type(type)
                                           .declaratorId(id)
                                           .build();
      this.parameters.add(param);
      return this;
    }

    public Builder addParameters(List<ASTFormalParameter> parameters){
      this.parameters.addAll(parameters);
      return this;
    }

    public Builder addBodyElement(String element){
      this.bodyElements.add(element);
      return this;
    }

    public Builder addBodyElements(List<String> bodyElements){
      this.bodyElements.addAll(bodyElements);
      return this;
    }

    public Builder setName(String name){
      this.name = name;
      return this;
    }

    public Constructor build(){
      ASTFormalParameters formalParameters;
      if(this.parameters.isEmpty()){
        formalParameters = ASTFormalParameters.getBuilder().build();
      } else{
        ASTFormalParameterListing listing =
            ASTFormalParameterListing
                .getBuilder()
                .formalParameters(this.parameters)
                .build();
        formalParameters = ASTFormalParameters
                               .getBuilder()
                               .formalParameterListing(listing)
                               .build();
      }
      return new Constructor(name, formalParameters, bodyElements);
    }
  }

  @Override public String toString() {
    return "Constructor{" +
        "name='" + name + '\'' +
        ", parameters=" + parameters +
        ", bodyElements=" + bodyElements +
        '}';
  }
}
