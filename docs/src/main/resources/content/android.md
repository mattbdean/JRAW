# Android

The [JRAW-Android](https://github.com/mattbdean/JRAW-Android) projects provides some Android-specific helper classes. It provides an [example app](https://github.com/mattbdean/JRAW-Android/tree/master/example-app) that implements the JRAW auth flow

## Getting Started

Include the dependency:

```groovy
repositories {
    maven { url "https://jitpack.io" }
}
dependencies {
    implementation "com.github.mattbdean:JRAW-Android:${version}"
}
```

`${version}` can be a tag (like `v1.0.0`), a specific commit (like `9390529`), or a branch (like `master-SNAPSHOT`).

Before using this library it is highly recommended that you first read the [OAuth2 page](oauth2.md).

First create a reddit OAuth2 app [here](https://www.reddit.com/prefs/apps). Note the client ID and redirect URL, you'll need these later.

Add these `<meta-data>` keys to your manifest:

```xml
<application>
    ...
    <meta-data
        android:name="net.dean.jraw.android.REDDIT_USERNAME"
        android:value="(...)" />
    <meta-data
        android:name="net.dean.jraw.android.CLIENT_ID"
        android:value="(...)" />
    <meta-data
        android:name="net.dean.jraw.android.REDIRECT_URL"
        android:value="(...)" />
</application>
```

The `REDDIT_USERNAME` key is used to create a UserAgent for your app. See [here](https://github.com/mattbdean/JRAW-Android/blob/master/lib/src/main/kotlin/net/dean/jraw/android/ManifestAppInfoProvider.kt) for more details.

Create your `Application` class:

```java
public final class App extends Application {
    private static AccountHelper accountHelper;
    private static SharedPreferencesTokenStore tokenStore;

    @Override
    public void onCreate() {
        super.onCreate();

        // Get UserAgent and OAuth2 data from AndroidManifest.xml
        AppInfoProvider provider = new ManifestAppInfoProvider(getApplicationContext());

        // Ideally, this should be unique to every device
        UUID deviceUuid = UUID.randomUUID();

        // Store our access tokens and refresh tokens in shared preferences
        tokenStore = new SharedPreferencesTokenStore(getApplicationContext());
        // Load stored tokens into memory
        tokenStore.load();
        // Automatically save new tokens as they arrive
        tokenStore.setAutoPersist(true);

        // An AccountHelper manages switching between accounts and into/out of userless mode.
        accountHelper = AndroidHelper.accountHelper(provider, deviceUuid, tokenStore);

        // Every time we use the AccountHelper to switch between accounts (from one account to
        // another, or into/out of userless mode), call this function
        accountHelper.onSwitch(redditClient -> {
            // By default, JRAW logs HTTP activity to System.out. We're going to use Log.i()
            // instead.
            LogAdapter logAdapter = new SimpleAndroidLogAdapter(Log.INFO);

            // We're going to use the LogAdapter to write down the summaries produced by
            // SimpleHttpLogger
            redditClient.setLogger(
                    new SimpleHttpLogger(SimpleHttpLogger.DEFAULT_LINE_LENGTH, logAdapter));

            // If you want to disable logging, use a NoopHttpLogger instead:
            // redditClient.setLogger(new NoopHttpLogger());

            return null;
        });
    }

    public static AccountHelper getAccountHelper() { return accountHelper; }
    public static SharedPreferencesTokenStore getTokenStore() { return tokenStore; }
}
```

Now you can start using JRAW! The [example app](https://github.com/mattbdean/JRAW-Android/tree/master/example-app) fully implements the reddit authentication process. I highly encourage you to build and install the app and read the source code to get a better understanding of the whole process.
