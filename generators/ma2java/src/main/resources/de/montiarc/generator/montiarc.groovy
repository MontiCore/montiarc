package de.montiarc.generator

// M1: configuration object "_configuration" prepared externally
info("--------------------------------")
info("MontiArc Generator")
info("--------------------------------")
debug("Model path     : " + modelPath)
debug("Output dir     : " + out.getAbsolutePath())
debug("Models         : " + models)
debug("Model          : " + model)
debug("HWCPath        : " + handcodedPath)
debug("--------------------------------")
if(!model.isEmpty()){
  generate(modelPath, model, out, handcodedPath);
}
else{
  generate(modelPath, out, handcodedPath);
}
info("--------------------------------")
info("MontiArc Generator END")
info("--------------------------------")
