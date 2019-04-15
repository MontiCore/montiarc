package de.montiarcautomaton.generator.codegen.componentinstantiation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.montiarcautomaton.runtimes.componentinstantiation.ILoader;

/**
 * Loads new components from disc. Components can be provided as Models, Java
 * Files or compiled code.
 */
public class FileSystemLoader implements ILoader {

	Map<String, AdapterLoader> adapterLoaders = new HashMap<>();
	Map<String, Object> classObjects = new HashMap<>();;
	private List<String> componentStores;
	AdapterLoader adapterLoader = new AdapterLoader();
	Object classObject = null;

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
			isStopped = true;
	}

	public FileSystemLoader() {
	}
	
	@Override
	/**
	 * @param instanceName Fully qualified instance name of Component that should be
	 *                     checked
	 * @param storeDir     Directory where new component files will be stored
	 * @param targetDir    Directory where generated and compiled sources should be
	 *                     moved
	 */
	public void init(String instanceName, String storeDir, String targetDir, List<String> subcomps,
			Map<String, List<String>> interfaces, Map<String, String> subcompTypes) {
		this.subcomps = subcomps;
		this.targetDir = targetDir;
		this.storeDir = storeDir;
		componentStores = new ArrayList<>();
		for (String sc : subcomps) {
			componentStores.add(sc + "Store.");

		}

		ComponentStore = instanceName + ".subPrinterStore.";
		filePath = storeDir + ComponentStore.replaceAll("\\.", "/");
		targetPath = targetDir + ComponentStore.replaceAll("\\.", "/");
		start();
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
	 * Generates Java Files from arc models in the store directory, compiles them
	 * and stores them in the Maps.
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
			adapterLoaders.get(store).compileClasses(targetDir + store.replaceAll("\\.", "/"));

			classObjects.put(store, adapterLoaders.get(store).getClassObject(targetDir + store.replaceAll("\\.", "/"),
					store, targetDir));
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

}
