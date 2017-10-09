# OAuth2

reddit uses OAuth2 to control access to its API. JRAW provides some utilities to make this (generally painful) process as painless as possible. If you'd like to know what JRAW is actually doing, make sure to read this [wiki page](https://github.com/reddit/reddit/wiki/OAuth2)!

## Types of OAuth2 Apps

reddit provides three types of OAuth2 apps:

 - **Web app**: Runs as part of a web service on a server you control. Can keep a secret.
 - **Installed app**: Runs on devices you don't control, such as the user's mobile phone. Cannot keep a secret, and therefore, does not receive one.
 - **Script app**: Runs on hardware you control, such as your own laptop or server. Can keep a secret. Only has access to your account.

If you're not sure which app type is best for you, check out [this page](https://github.com/reddit/reddit/wiki/oauth2-app-types).

Normally, these apps require a user to allow the app access to their account. This isn't always necessary, however. An installed or web app can utilize "application-only" or **"userless"** mode, which allows it access to the API without a user.

This makes for a total of five different [[@net.dean.jraw.oauth.AuthMethod]]s.

To create a reddit OAuth2 app, see [here](https://www.reddit.com/prefs/apps).

## Automatic Authentication

Automatic authentication is used for **script and userless apps** and can be done in one line of code!

{{ OAuth2.automatic }}

## Interactive Authentication

Interactive authentication is used for **installed and web apps** (e.g. Android apps) and is a little more complex. The general process goes something like this:

 1. Generate an authorization URL for the user.
 2. Direct the user to this URL. This will prompt them to either allow or deny your app access to their account.
 3. After the user decides, reddit will redirect them to the OAuth2 app's `redirect_uri` with some extra data in the query.
 4. Verify this data and request the access token.

JRAW handles steps 1, 3, and 4 for you. It's your job to show the user the authorization URL. Here's a quick example of what this process might look like:

{{ OAuth2.interactive }}

If your app is using userless mode, you can use automatic authentication instead!

## Renewing an Access Token

Access tokens are what enables us to send requests to `oauth.reddit.com` successfully. These expire one hour after they were requested. JRAW is capable of refreshing access tokens automatically if it has the right data.

**Automatic authentication**

If you're using automatic authentication, you don't have to do anything! JRAW automatically requests new data the same way it was done originally.

**Interactive authentication**

In order for JRAW to get a new access token, it must use a refresh token. A refresh token is a special string unique to your app that allows the client to request new access tokens. Refresh tokens don't expire, so once you have one, you can requests new access tokens until the refresh token is manually revoked.

To make sure JRAW has access to a refresh token, make sure you pass `true` for `requestRefreshToken` in `StatefulAuthHelper.getAuthorizationUrl`:

{{ OAuth2.requestRefreshToken }}

**Manually renewing**

If for some reason you don't want JRAW to automatically request new data for you, you can disable this feature and refresh manually like this:

{{ OAuth2.manualRenewing }}

## Revoking an Access or Refresh Token

As you may have learned from before, access tokens expire after one hour and refresh tokens don't expire. If your app doesn't need a token, it's generally a good idea to revoke it. In this case, when we revoke a token, we're really telling reddit that we're done with this token and that it should revoke the token's ability to interact with the API.

{{ OAuth2.revokeTokens }}

You're not required to do this, but it reduces the chance that an attacker could use an old access or refresh token to gain access to a user's account.

## TokenStores

Writing an installed app can be much more complex task than a small script. How do you keep track of access and refresh tokens? How do you handle different users? How do you prevent an additional access token request when there's already an unexpired token floating out there?

JRAW attempts to solve this problem with [[@TokenStore]]s. A TokenStore stores, retrieves, and deletes access and refresh tokens for different users. Usually this data should be saved somewhere more permanent (like a file).

TokenStores can be given as a parameter to [[@OAuthHelper]] methods:

{{ OAuth2.tokenStoreParameter }}

Whenever the RedditClient refreshes its access token, it will also notify its TokenStore!

TokenStores aren't incredibly useful by themselves. For the best results, use an AccountHelper as well.

## AccountHelper

Creating a reddit app for Android or the web can be quite daunting. Developers have to manage different users, switching between users and into/out of userless mode, and storing and organizing all of this data. JRAW tries to make this as easy as possible.

As an additional level of abstraction for Android developers, JRAW includes the [[@AccountHelper]] class. Think of AccountHelper like a factory for RedditClients. You give the factory some basic information (a NetworkAdapter, OAuth2 app credentials, etc.) and it produces authenticated RedditClients for you.

Here's a quick tour:

{{ OAuth2.accountHelper }}
