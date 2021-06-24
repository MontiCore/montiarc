/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;

import java.util.Optional;

public class MontiArcTypesSynthesizer implements ISynthesize {
  
  MontiArcTraverser traverser;
  TypeCheckResult typeCheckResult;
  
  public MontiArcTypesSynthesizer() {
    init();
  }
  
  @Override
  public Optional<SymTypeExpression> getResult() {
    return Optional.ofNullable(typeCheckResult.getCurrentResult());
  }
  
  @Override
  public void init() {
    traverser = MontiArcMill.traverser();
    
    typeCheckResult = new TypeCheckResult();
    
    SynthesizeSymTypeFromMCBasicTypes synBasicTypes = new SynthesizeSymTypeFromMCBasicTypes();
    synBasicTypes.setTypeCheckResult(typeCheckResult);
    traverser.add4MCBasicTypes(synBasicTypes);
    traverser.setMCBasicTypesHandler(synBasicTypes);
  
    SynthesizeSymTypeFromMCCollectionTypes synCollectionTypes = new SynthesizeSymTypeFromMCCollectionTypes();
    synCollectionTypes.setTypeCheckResult(typeCheckResult);
    traverser.add4MCCollectionTypes(synCollectionTypes);
    traverser.setMCCollectionTypesHandler(synCollectionTypes);
  
    SynthesizeSymTypeFromMCSimpleGenericTypes synSimpleGenericTypes = new SynthesizeSymTypeFromMCSimpleGenericTypes();
    synSimpleGenericTypes.setTypeCheckResult(typeCheckResult);
    traverser.add4MCSimpleGenericTypes(synSimpleGenericTypes);
    traverser.setMCSimpleGenericTypesHandler(synSimpleGenericTypes);
  }
  
  @Override
  public MCBasicTypesTraverser getTraverser() {
    return traverser;
  }
}