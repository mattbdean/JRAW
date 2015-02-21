package net.dean.jraw.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dean.jraw.http.oauth.Credentials;

import java.io.IOException;
import java.io.InputStream;

public final class CredentialsUtils {
    private static CredentialsUtils INSTANCE = new CredentialsUtils();

    public static CredentialsUtils instance() { return INSTANCE; }

    private Credentials script;
    private Credentials installed;

    private CredentialsUtils() {
        String username, password, scriptId, scriptSecret, installedId, installedRedirectUri;
        // If running locally, use credentials file
        // If running with Travis-CI, use environmental variables
        if (System.getenv("TRAVIS") != null && Boolean.parseBoolean(System.getenv("TRAVIS"))) {
            username = System.getenv("USERNAME");
            password = System.getenv("PASSWORD");
            scriptId = System.getenv("SCRIPT_CLIENT_ID");
            scriptSecret = System.getenv("SCRIPT_CLIENT_SECRET");
            installedId = System.getenv("INSTALLED_CLIENT_ID");
            installedRedirectUri = System.getenv("INSTALLED_REDIRECT_URI");
        } else {
            // Read credentials.json
            InputStream in = RedditTest.class.getResourceAsStream("/credentials.json");
            if (in == null) {
                throw new SetupRequiredException("credentials.json could not be found.");
            }

            JsonNode data;
            try {
                data = new ObjectMapper().readTree(in);
            } catch (IOException e) {
                throw new RuntimeException("Could not read credentials.json", e);
            }

            try {
                username = data.get("user").get("username").asText();
                password = data.get("user").get("password").asText();
                scriptId = data.get("script").get("client_id").asText();
                scriptSecret = data.get("script").get("client_secret").asText();
                installedId = data.get("installed").get("client_id").asText();
                installedRedirectUri = data.get("installed").get("redirect_uri").asText();
            } catch (NullPointerException e) {
                throw new SetupRequiredException("Missing key in credentials.json.");
            }
        }

        this.script = Credentials.script(username, password, scriptId, scriptSecret);
        this.installed = Credentials.installedApp(username, password, installedId, installedRedirectUri);
    }

    public Credentials script() {
        return script;
    }

    public Credentials installedApp() {
        return installed;
    }
}
