package de.montiarcautomaton.generator.codegen.componentinstantiation;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.montiarcautomaton.runtimes.componentinstantiation.ILoader;



/**
 * Loads new components from disc. Components can be provided as Models,
 * Java Files or compiled code.
 */
public class WebShopLoader implements ILoader {

	private Map<String, AdapterLoader> adapterLoaders = new HashMap<>();
	private Map<String, Object> classObjects = new HashMap<>();
	private Map<String, List<String>> interfaces = new HashMap<>();
	private Map<String, Instant> timestamps = new HashMap<>();
	private Map<String, String> subcompTypes = new HashMap<>();;

	private List<String> componentStores;
	private AdapterLoader adapterLoader = new AdapterLoader();
	private Object classObject = null;


	private static String ComponentStore = null;

	private String filePath;
	private String storeDir;
	private String targetDir;
	private String targetPath = null;
	private Thread t;
	private List<String> subcomps;
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
	public WebShopLoader(String instanceName, String storeDir, String targetDir,
			List<String> subcomps) {
		this.subcomps = subcomps;
		this.targetDir = targetDir;
		this.storeDir = storeDir;
		componentStores = new ArrayList<>();
		for (String sc : subcomps) {
			componentStores.add(sc + "Store.");

		}

		filePath = storeDir
				+ ComponentStore.replaceAll("\\.", "/");
		targetPath = targetDir
				+ ComponentStore.replaceAll("\\.", "/");
	}

	public WebShopLoader(String instanceName, String storeDir, String targetDir,List<String> subcomps,
				Map<String, List<String>> interfaces, Map<String,String> subcompTypes) {
		this(instanceName, storeDir,  targetDir, subcomps);

		for (String sc : subcomps) {
			timestamps.put(sc, Instant.now());
		}
		this.interfaces = interfaces;
		this.subcompTypes = subcompTypes;
	}

	@Override
	/**
	 * Used by components to check if a new subcomponent has been found 
	 */
	public Optional<Object> hasNewSubComponent(String name) {
		String key = name + "Store.";
		if (classObjects.get(key) == null || adapterLoaders.get(key) == null) {
			return Optional.empty();
		}
		adapterLoaders.put(key, null);
		return Optional.of(classObjects.get(key));
	}



	@Override
	/**
	 * Generates Java Files from arc models in the store directory, compiles them and stores
	 * them in the Maps.
	 */
	public void checkForUpdate() {
		adapterLoader.generateJavaFiles(storeDir, targetDir);
		if (adapterLoader == null) {
			classObject = null;
			adapterLoader = new AdapterLoader();
		}

		if (classObject != null) {
			return;
		}
		
		for (String store : componentStores) {
			System.out.println("Check for update in " + store);
			if (adapterLoaders.get(store) == null) {
				classObjects.put(store, null);
				adapterLoaders.put(store, new AdapterLoader());
			}
			if (classObjects.get(store) != null) {
				return;
			}
			checkWebStore(store);
			adapterLoaders.get(store).compileClasses(targetDir + store.replaceAll("\\.", "/"));
			classObjects.put(store,adapterLoaders.get(store).getClassObject(targetDir + store.replaceAll("\\.", "/"), store, targetDir));
		}
	}


	/**
	 * Deletes compiled files from the directory specified by the input
	 */
	@Override
	public void deleteFile(String name) {
		String fileString = targetDir + name + "Store";
		String filePath = fileString.replaceAll("\\.", "/");
		new AdapterLoader().deleteClassFile(filePath);
	}
	
	private void checkWebStore(String store) {
		String subCompName = store.substring(0, store.length()-6);
		String timestamp = timestamps.get(subCompName).toString();
		List<String> subCompinterface = interfaces.get(subCompName);
		String subCompType = subcompTypes.get(subCompName);
		Path path = Paths.get(this.storeDir + store.replaceAll("\\.", "/"));
		
		//TODO: Use timestamp, subCompInterface, subCompType for REST call, store resulting
		// file in path and update timestamp afterwards
	}



}
