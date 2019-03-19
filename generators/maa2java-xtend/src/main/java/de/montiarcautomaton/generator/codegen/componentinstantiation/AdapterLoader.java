package de.montiarcautomaton.generator.codegen.componentinstantiation;


import de.montiarcautomaton.generator.MontiArcGeneratorTool;
import de.se_rwth.commons.Files;
import org.apache.commons.io.FileUtils;

import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class AdapterLoader extends ClassLoader {

  private String className = null;
  private String classDir;


  /**
   * Deletes all files in directory
   * @param filePath FIlepath that will be deleted
   */
  public void deleteClassFile(String filePath) {

    try {
      FileUtils.cleanDirectory(Paths.get(filePath).toFile());
    } catch (IOException e) {
      e.printStackTrace();
    }

  }


  /**
   * Compiles all Java files in specified directory. Since the Java compiler needs
   * to access every file in a package when compiling single files, this should always
   * be the whole component store instead of a specific folder containing only
   * a part of the components.
   * @param filePath
   */
  public void compileClasses(String filePath) {
    String classPathString = filePath;
    File classPath = Paths.get(classPathString).toFile();
    if (!classPath.isDirectory()){
      return;
    }
    List<File> classFiles = Arrays.stream(classPath.listFiles())
            .filter(file -> file.getName().endsWith(".java"))
            .collect(Collectors.toList());
    if (classFiles.size() == 0) {
      return;
    }

    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    System.out.println(compiler.toString());
    StandardJavaFileManager fileManager = compiler.getStandardFileManager(null,
            null, null);
    //TODO: Do something with collected Diagnostics
    //DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(classFiles);

   
    CompilationTask task = compiler.getTask(null, fileManager, null, Arrays.asList("-sourcepath", Paths.get(filePath).toAbsolutePath().toString()), null, compilationUnits);
    if (task.call()) {
    	System.out.println("Compilation done!");
	}
    try {
      fileManager.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    for (File classFile : classFiles) {
      Files.deleteFiles(classFile);
    }
  }



  /**
   * Generate Java Files out of MontiArc models. Can only be invoked on a whole package folder. Using it on
   * folders that are not the top level of a particular package will lead to errors.
   * @param filePath
   * @param targetPath
   */
  public void generateJavaFiles(String filePath, String targetPath){
    MontiArcGeneratorTool tool = new MontiArcGeneratorTool();
    tool.enableDynamicGeneration(true);

    File modelFile = Paths.get(filePath + "/").toFile();

    File targetFile = Paths.get(targetPath).toFile();

    if (!modelFile.isDirectory()) {
    	System.out.println("Model Path not found! Did you set model directory? Dir: " + modelFile.toString());
    }
    List<File> files = (List<File>) FileUtils.listFiles(modelFile, new String[]{"arc"}, true);

    if (files.size() > 0) try {

      Thread.sleep(1000);
      tool.generate(modelFile, targetFile, modelFile);
      for (File file : files) {
        Files.deleteFiles(file);
      }
      FileUtils.copyDirectory(modelFile,targetFile);
      FileUtils.cleanDirectory(modelFile);


    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }

  }

  /**
   * Returns class objects from compiles java Files.
   * @param path Path containing compiled Files
   * @param className Name of the Class to be loaded
   * @return
   */
  public Object getClassObject(String path, String className, String classDir) {
    //String classPathString = "applications/prototype/target/classes/"
    //        + path.replaceAll("\\.", "/");
	  this.classDir = classDir;
    File classPath = Paths.get(path).toFile();
    String newClassPath = null;
    String fullClassName = null;
    if (classPath.exists() && classPath.isDirectory()) {
      for (File file : classPath.listFiles()) {
        String name = file.getName().split("\\.")[0];
        if (name.startsWith("Dynamic") && !name.endsWith("Impl") && !name.endsWith("Input") && !name.endsWith("Result")) {
          newClassPath = path + name;
          fullClassName = className + name;
        }
      }
    }
    if (newClassPath != null) {
      try {
        return this.loadClass(fullClassName);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
    return null;

  }

  /**
   * Read compiled file from disc if the input name cotains the keyword 'store'. This defers to the
   * default class loader implementation of loadClass in all other cases. This convoluted implementation
   * is necessary since after loading a class with this method all other referenced classes inside the
   * loaded class will be attempted to be loaded with this method as well. This is a problem for all classes
   * that were already loaded with the default classloader.
   * @param name
   * @return
   * @throws ClassNotFoundException
   */
  public Class loadClass(String name) throws ClassNotFoundException {
    if (!name.contains("Store"))
      return super.loadClass(name);

    try {
      URL url = Paths.get(classDir
              + name.replaceAll("\\.", "/")
              + ".class")
              .toUri().toURL();
      URLConnection connection = url.openConnection();
      InputStream input = connection.getInputStream();
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      int data = input.read();
      while (data != -1) {
        buffer.write(data);
        data = input.read();
      }
      input.close();
      byte[] classData = buffer.toByteArray();
      //Create Class
      //Prunce .class extension and target/classes prefix

      return defineClass(name,
              classData, 0, classData.length);

    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }
}
