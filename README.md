# OpenStitch

## Develop

In order to start developing there's a couple things you need to setup first.

- Create a developer account on Ravelry
- Go to apps in the admin UI, and add a new app with Oauth2.0 credentials
- In the settings for the app add `openstitch://oauth-callback/ravelry` to the `Authorized Redirect URIs`
    - NOTE: The callback must have `openstitch` as the scheme since it is defined in this Android apps manifest as `openstitch`
- Create a `gradle.properties` file in this Android app at the root directory
- Take the credentials generated from that app, and paste them into the `gradle.properties` file like:
```
CLIENT_KEY="client_id_from_ravelry_app"
CLIENT_SECRET="secret_from_ravelry_app"
CLIENT_REDIRECT_URL="openstitch://oauth-callback/ravelry"
```

Now you should be ready to build and launch the app locally!

### More resources

- Ravelry Api Docs - https://www.ravelry.com/api