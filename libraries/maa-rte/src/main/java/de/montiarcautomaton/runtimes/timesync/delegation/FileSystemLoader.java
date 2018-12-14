package de.montiarcautomaton.runtimes.timesync.delegation;

import java.util.Optional;


/**
 * Loads new components from disc. Components can be provided as Models,
 * Java Files or compiled code.
 */
public class FileSystemLoader implements Runnable {

  AdapterLoader adapterLoader = null;
  Object classObject = null;

  private static String ComponentStore = null;
  private String filePath;
  private String storeDir;
  private String targetDir;
  private String targetPath = null;
  private Thread t;
  private volatile boolean isStopped = false;


  /**
   * Starts Thread to continuously check for updated components
   */
  public void start() {
    System.out.println("Starting FileSystemLoader!");
    if (t == null) {
      t = new Thread(this, "FileSystemLoader");
      t.start();
      System.out.println("Started Thread.");
    }

  }

  /**
   * Check for updates as long as stop flag isn't set
   */
  @Override
  public void run() {
    while (!isStopped) {
      System.out.println("Checking for Printer Update");
      checkForUpdate();
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Toggle flag to stop the thread. Is checked in the run method.
   */
  public void stop() {
    if (isStopped == true) isStopped = true;
  }


  /**
   * @param instanceName Fully qualified instance name of Component that should be checked
   * @param storeDir     Directory where new component files will be stored
   * @param targetDir    Directory where generated and compiled sources should be moved
   */
  public FileSystemLoader(String instanceName, String storeDir, String targetDir) {
//    ComponentStore = instanceName + ".subPrinterStore.";
//    filePath = "applications/prototype/target/componentstore/"
//            + ComponentStore.replaceAll("\\.", "/");
//    targetPath = "applications/prototype/target/classes/"
//            + ComponentStore.replaceAll("\\.", "/");
    this.targetDir = targetDir;
    this.storeDir = storeDir;
    ComponentStore = instanceName + ".subPrinterStore.";
    filePath = storeDir
            + ComponentStore.replaceAll("\\.", "/");
    targetPath = targetDir
            + ComponentStore.replaceAll("\\.", "/");
  }

  /**
   * Returns class file if an updated component has been found.
   *
   * @return Class file of updated component.
   */
  public Optional<Object> hasNewSubPrinter() {
    if (classObject == null || adapterLoader == null) {
      return Optional.empty();
    }
    adapterLoader = null;
    return Optional.of(classObject);
  }

  /**
   *
   */
  public void checkForUpdate() {

    if (adapterLoader == null) {
      classObject = null;
      adapterLoader = new AdapterLoader();
    }

    if (classObject != null) {
      return;
    }

    adapterLoader.generateJavaFiles("applications/prototype/target/componentstore/"
            , "applications/prototype/target/classes/");
    adapterLoader.compileClasses(targetPath);
    //FileUtils.copyDirectory(Paths.get(filePath).toFile(),
    //        Paths.get(targetPath).toFile());
    //FileUtils.cleanDirectory(Paths.get(filePath).toFile());
    //adapterLoader.deleteClassFile(filePath);
    classObject = adapterLoader.getClassObject(targetPath, ComponentStore);

  }


  public void deleteFile() {
    String filePath = storeDir + ComponentStore.replaceAll("\\.", "/");

    String targetPath = targetDir + ComponentStore.replaceAll("\\.", "/");
    new AdapterLoader().deleteClassFile(targetPath);
    //new AdapterLoader().deleteClassFile(filePath);

  }


}
