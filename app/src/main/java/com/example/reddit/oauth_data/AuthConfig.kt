package com.example.reddit.oauth_data

import net.openid.appauth.ResponseTypeValues

object AuthConfig {
    const val CLIENT_ID = "Q52cE1-2GHaCZB07G-bujQ"
    const val REDIRECT_URL = "reddit://com.example.reddit/redirect"
    const val CLIENT_SECRET = ""
    const val OAUTH_URL = "https://www.reddit.com/api/v1/authorize"
    const val GRANT_TYPE_REFRESH = "refresh_token"
    const val GRANT_TYPE2 = "authorization_code"
    const val TOKEN_URL = "https://www.reddit.com/api/v1/access_token"
    const val OAUTH_SCOPE = "read history edit identity mysubreddits privatemessages submit vote subscribe save"
    const val DURATION = "permanent"
    const val RESPONSE_TYPE = ResponseTypeValues.CODE
    const val ACCESS_PREF_KEY = "access_token_key"
}