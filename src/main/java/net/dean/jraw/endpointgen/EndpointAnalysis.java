package net.dean.jraw.endpointgen;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class is responsible for the compilation of <a href="https://github.com/thatJavaNerd/JRAW/blob/master/ENDPOINTS.md">ENDPOINITS.md</a>,
 * which is a collection of implemented and unimplemented Reddit API endpoints.
 */
public class EndpointAnalysis {
	private static final String FILE_NAME = "ENDPOINTS.md";
	private static final String ALL_ENDPOINTS_FILE_NAME = "endpoints.json";

	/** The file that represents {@value #FILE_NAME} */
	private File exportFile;
	/** The file that contains a JSON mapping of all the endpoints and their respective categories */
	private File jsonEndpoints;
	/** The ObjectMapper used to read {@value #ALL_ENDPOINTS_FILE_NAME} file */
	private ObjectMapper mapper;
	/** The DateFormat that will be used to generate the timestamp comment at the beginning of the file */
	private SimpleDateFormat dateFormat;

	/**
	 * Instantiates a new EndpointAnalysis
	 */
	public EndpointAnalysis() {
		this.dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
		this.mapper = new ObjectMapper();
		try {
			this.jsonEndpoints = new File(EndpointAnalysis.class.getResource("/" + ALL_ENDPOINTS_FILE_NAME).toURI());
			// Json file located in "JRAW/build/resources/main/" when run, go up 4 directories
			this.exportFile = jsonEndpoints;
			for (int i = 0; i < 4; i++) {
				exportFile = exportFile.getParentFile();
			}
			// "JRAW/ENDPOINTS.md"
			exportFile = new File(exportFile, FILE_NAME);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets a list of implemented and unimplemented endpoints and writes them to the {@link #exportFile}.
	 */
	public void run() {
		List<List<Endpoint>> allEndpoints = getEndpoints();
		List<Endpoint> unimplemented = allEndpoints.get(0);
		List<Endpoint> implemented = allEndpoints.get(1);

		int allEndpointsSize = 0;
		for (List<Endpoint> endpoints : allEndpoints) {
			allEndpointsSize += endpoints.size();
		}

		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(exportFile), StandardCharsets.UTF_8))) {
			// http://stackoverflow.com/a/4829998/1275092
			bw.write(String.format("<!--- Generated %s. Do ./gradlew updateEndpoints to update. DO NOT MODIFY DIRECTLY -->\n",
					dateFormat.format(new Date())));

			// Write both maps
			exportMap(bw, unimplemented, "Unimplemented", allEndpointsSize);
			exportMap(bw, implemented, "Implemented", allEndpointsSize);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes a list of endpoints with a BufferedWriter with a particular title
	 *
	 * @param bw The BufferedWriter to use
	 * @param endpoints The list of endpoints to write
	 * @param title The title of the list
	 *
	 * @throws IOException If there was a problem writing to the file
	 */
	private void exportMap(BufferedWriter bw, List<Endpoint> endpoints, String title, int allEndpointsSize) throws IOException {
		// TreeMap<category, list of endpoints>
		TreeMap<String, List<String>> unimplMap = new TreeMap<>();
		for (Endpoint endpoint : endpoints) {
			// Initialize the list if it hasn't been already
			if (unimplMap.get(endpoint.getCategory()) == null) {
				unimplMap.put(endpoint.getCategory(), new ArrayList<>());
			}

			unimplMap.get(endpoint.getCategory()).add(endpoint.getUri());
		}

		// Main header of collection
		bw.write(String.format("#%s (%s/%s)\n", title, endpoints.size(), allEndpointsSize));;

		// Iterate through the entries and write them to the file
		for (Map.Entry<String, List<String>> entry : unimplMap.entrySet()) {
			// Write the category header
			bw.write(String.format("####%s\n", entry.getKey()));
			// Start the code block
			bw.write("~~~\n");
			// Write every endpoint in that category
			for (String endpoint : entry.getValue()) {
				bw.write(endpoint + "\n");
			}
			// End the code block
			bw.write("~~~\n\n");
		}
	}

	/**
	 * Gets a list of lists of endpoints in which the first list is a list of unimplemented endpoints and the second list
	 * is a list of implemented endpoints
	 *
	 * @return A list of lists of endpoints where the first list contains unimplemented endpoints and the second is a list
	 * of implemented endpoints
	 */
	private List<List<Endpoint>> getEndpoints() {
		List<Endpoint> unimplemented = getAllEndpoints();
		List<Endpoint> implemented = new ArrayList<>();

		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setUrls(ClasspathHelper.forPackage("net.dean.jraw"))
				.setScanners(new MethodAnnotationsScanner()));

		Set<Method> methods = reflections.getMethodsAnnotatedWith(EndpointImplementation.class);

		for (Method m : methods) {
			EndpointImplementation endpoint = m.getAnnotation(EndpointImplementation.class);
			for (String uri : endpoint.uris()) {
				// Use Iterator to prevent a ConcurrentModificationException
				for (Iterator<Endpoint> it = unimplemented.iterator(); it.hasNext();) {
					Endpoint un = it.next();
					if (un.getUri().equals(uri)) {
						it.remove();
						implemented.add(un);
					}
				}
			}
		}

		List<List<Endpoint>> returnVal = new ArrayList<>(2);
		returnVal.add(unimplemented);
		returnVal.add(implemented);

		return returnVal;
	}

	/**
	 * Reads {@link #jsonEndpoints} and parses Endpoint objects from it
	 *
	 * @return A list of all the endpoints noted in the file
	 */
	private List<Endpoint> getAllEndpoints() {
		List<Endpoint> endpoints = new ArrayList<>();

		try {
			JsonNode rootNode = mapper.readTree(jsonEndpoints);

			for (Iterator<Map.Entry<String, JsonNode>> it = rootNode.getFields(); it.hasNext(); ) {
				Map.Entry<String, JsonNode> entry = it.next();

				for (JsonNode endpoint : entry.getValue()) {
					endpoints.add(new Endpoint(endpoint.asText(), entry.getKey()));
				}
			}

			return endpoints;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void main(String[] args) throws IOException {
		new EndpointAnalysis().run();
	}
}
