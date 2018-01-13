package net.dean.jraw.gradle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.jsoup.Jsoup

import java.security.SecureRandom

class CredentialsTask extends DefaultTask {
    private static Random random = new SecureRandom()

    private Scanner input = new Scanner(System.in)
    private String modhash
    private String cookie
    private OkHttpClient http = new OkHttpClient()
    private ObjectMapper jsonHelper = new ObjectMapper()

    CredentialsTask() {
        description = 'create OAuth2 apps for a given testing user'
    }

    @TaskAction
    def create() {
        def username = prompt("Testing user username:")
        def password = prompt("Testing user password:")
        login(username, password)


        def sub
        try {
            // Any user can create an OAuth2 app, but not every user can create a testing subreddit. We don't want to
            // discard the created apps just because we can't create a subreddit. Handle problems that occur here
            // individually.
            sub = createSubreddit()
        } catch (e) {
            System.err.println("Unable to create a subreddit:")
            sub = "<unable to create testing subreddit>"
            e.printStackTrace()
        }

        def data = [
            'app': createApp(username, password, AppType.INSTALLED),
            'script': createApp(username, password, AppType.SCRIPT),
            'moderationSubreddit': sub
        ]

        println("\nPut this in lib/src/test/resources/credentials.json:\n")
        prettyPrint(data)
    }

    private void login(String username, String password) {
        // Try to login
        def res = http.newCall(new Request.Builder()
            .url(new HttpUrl.Builder()
            .scheme("https")
            .host("www.reddit.com")
            .addPathSegments("api/login/$username")
            .build())
            .post(formBody([
                "op": "login",
                "user": username,
                "passwd": password,
                "api_type": "json"
            ]))
            .build()
        ).execute()
        JsonNode json = jsonHelper.readTree(res.body().byteStream()).get("json")

        // Handle some basic errors
        if (json.has("errors") && json.get("errors").has(0)) {
            // Get the error code
            throw new IllegalStateException(json.get("errors").get(0).get(0).asText())
        }

        // Set the modhash and cookie variables for authenticated user later
        this.modhash = json.get("data").get("modhash").asText()
        this.cookie = json.get("data").get("cookie").asText()
    }

    private AppInfo createApp(String username, String password, AppType type) {
        def redirectUrl = "https://github.com/mattbdean/JRAW"
        def res = http.newCall(new Request.Builder()
            .url("https://www.reddit.com/api/updateapp")
            .addHeader("cookie", "reddit_session=${URLEncoder.encode(this.cookie, "UTF-8")}")
            .post(formBody([
                "uh": modhash,
                "name": "JRAW test - ${type.name().toLowerCase()} - ${randomString().substring(0, 5)}",
                "app_type": type.name().toLowerCase(),
                "description": "This app was automatically created for you by a JRAW script",
                "about_url": "https://github.com/thatJavaNerd/JRAW",
                "redirect_uri": redirectUrl
            ]))
            .build()
        ).execute()

        def json = jsonHelper.readTree(res.body().string())
        if (!json.get("success")) {
            println(prettyPrint(json))
            throw new IllegalStateException("could not create app with type '$type'")
        }

        // reddit does that thing where it returns a response made for jQuery, fetch the content manually. The content
        // will include HTML entities like '&lt;', unescape those and parse into a Jsoup document
        def escapedHtml = json.get("jquery").get(22).get(3).get(0).asText()
        def doc = Jsoup.parse(basicHtmlUnescape(escapedHtml))

        // Magic CSS selector to find the client ID
        def clientId = doc.select("h3")[1].text()
        def fields = doc.select(".preftable td.prefright").dropRight(1)

        // Try to parse the client secret from the HTML, if any.
        String clientSecret = "<unknown>"
        for (field in fields) {
            if (field.text() != "") {
                clientSecret = field.text()
                break
            }
        }

        // Only return the info we need
        switch (type) {
            case AppType.INSTALLED:
                return new AppInfo(null, null, clientId, null, redirectUrl)
            case AppType.SCRIPT:
                return new AppInfo(username, password, clientId, clientSecret, null)
            case AppType.WEB:
                return new AppInfo(null, null, clientId, clientSecret, redirectUrl)
            default:
                throw new IllegalArgumentException("Unknown type $type")
        }
    }

    private String createSubreddit() {
        def id = randomString().substring(0, 10)
        def name = "jraw_test_" + id
        def res = http.newCall(new Request.Builder()
            .url("https://www.reddit.com/api/site_admin")
            .addHeader("cookie", "reddit_session=" + URLEncoder.encode(cookie, "UTF-8"))
            .post(formBody([
                "name": name,
                "title": name,
                "public_description": "Automatically created to test JRAW",
                "description": "Automatically created to test JRAW",
                "lang": "en",
                "type": "public",
                "link_type": "any",
                "wikimode": "disabled",
                "uh": modhash,
                "api_type": "json",
            ]))
            .build()).execute()

        def json = jsonHelper.readTree(res.body().byteStream())

        // Try to handle API errors
        if (json.has("json") && json.get("json").has("errors")) {
            def errors = json.get("json").get("errors")
            if (errors.isArray() && errors.size() > 0)
                throw new RuntimeException("Could not create subreddit: " + errors.get(0))
        }

        return name
    }

    /** Uses Jackson to pretty print the given value to the standard output */
    private void prettyPrint(val) {
        println(jsonHelper.writerWithDefaultPrettyPrinter().writeValueAsString(val))
    }

    /** Asks for input from the user */
    private String prompt(String message) {
        System.out.println(message)
        System.out.print("> ")
        System.out.flush()
        return input.nextLine()
    }

    /** Generates a random alpha-numeric string */
    private static String randomString() {
        return new BigInteger(130, random).toString(32)
    }

    /** Creates a form-encoded RequestBody out of the given key-value pairs */
    private static RequestBody formBody(Map<String, String> map) {
        def body = new FormBody.Builder()
        for (e in map) {
            body.add(e.key, e.value)
        }
        return body.build()
    }

    /** Replaces '<', '>', and '"' HTML entities with their literal values */
    private static String basicHtmlUnescape(String str) {
        return str.replace("&gt;", ">").replace("&lt;", "<").replace("&quot;", '"')
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(value = ["originalClassName", "contentHash"])
    class AppInfo {
        AppInfo(String username, String password, String clientId, String clientSecret, String redirectUrl) {
            this.username = username
            this.password = password
            this.clientId = clientId
            this.clientSecret = clientSecret
            this.redirectUrl = redirectUrl
        }

        String username
        String password
        String clientId
        String clientSecret
        String redirectUrl


        @Override
        String toString() {
            return "AppInfo{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                '}'
        }
    }

    enum AppType {
        WEB,
        INSTALLED,
        SCRIPT
    }
}

