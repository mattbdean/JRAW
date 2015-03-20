package net.dean.jraw.endpoints;

import net.dean.jraw.Endpoint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ReadmeUpdater extends AbstractEndpointGenerator {
    private static final String IMG_URL = "https://img.shields.io/badge/api--coverage-%s-blue.svg";
    private static final String SEARCH_REGEX =
            "http[s]?://img\\.shields\\.io/badge/api--coverage-(\\d{1,3})%-blue\\.svg";

    /**
     * Instantiates a new AbstractEndpointGenerator
     *
     * @param endpoints A list of endpoints
     */
    public ReadmeUpdater(List<Endpoint> endpoints) {
        super(endpoints, false);
    }

    @Override
    protected void _generate(File dest, IndentAwareFileWriter bw) throws IOException {
        double percentage = (getImplementedEndpointsCount() / (double) getTotalEndpoints()) * 100;
        int roundedPercentage = (int) Math.round(percentage);
        String percentageString = roundedPercentage + "%";
        String url = String.format(IMG_URL, percentageString);

        byte[] encoded = Files.readAllBytes(Paths.get(dest.toURI()));
        String readme = new String(encoded, StandardCharsets.UTF_8);
        readme = readme.replaceFirst(SEARCH_REGEX, url);

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(dest.toURI()), StandardCharsets.UTF_8)) {
            writer.write(readme);
        }
    }
}
