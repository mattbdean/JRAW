package net.dean.jraw.endpoints;

import net.dean.jraw.Endpoint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.NavigableMap;

public class ReadmeUpdater extends AbstractEndpointGenerator {
    private static final String SEARCH_REGEX =
            "http[s]?://img\\.shields\\.io/badge/api--coverage-(\\d{1,2}\\.\\d{2})%-blue\\.svg";

    private DecimalFormat format;

    /**
     * Instantiates a new AbstractEndpointGenerator
     *
     * @param endpoints A map of endpoints where the key is the category and the value is a list of endpoints in that category
     */
    public ReadmeUpdater(NavigableMap<String, List<Endpoint>> endpoints) {
        super(endpoints, false);
        this.format = new DecimalFormat("#.##");
    }

    @Override
    protected void _generate(File dest, BufferedWriter bw) throws IOException {
        double percentage = (getImplementedEndpointsCount() / (double) getTotalEndpoints()) * 100;
        String percentageString = format.format(percentage) + "%";
        String url = String.format("https://img.shields.io/badge/api--coverage-%s-blue.svg", percentageString);

        byte[] encoded = Files.readAllBytes(Paths.get(dest.toURI()));
        String readme = new String(encoded, StandardCharsets.UTF_8);
        readme = readme.replaceFirst(SEARCH_REGEX, url);

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(dest.toURI()))) {
            writer.write(readme);
        }
    }
}
