package de.monticore.cd2pojo

info("--------------------------------")
info("CD2POJO Generator")
info("--------------------------------")
debug("Model path     : " + modelPath)
debug("Output dir     : " + out.getAbsolutePath())
debug("--------------------------------")
generate(modelPath, out)
info("--------------------------------")
info("CD2POJO Generator END")
info("--------------------------------")