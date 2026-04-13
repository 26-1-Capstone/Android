package com.example.nutrishare_android.data.network

import android.webkit.CookieManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class WebViewCookieJar(
    private val cookieManager: CookieManager = CookieManager.getInstance()
) : CookieJar {

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookies.forEach { cookie ->
            cookieManager.setCookie(url.toString(), cookie.toString())
        }
        cookieManager.flush()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val rawCookies = cookieManager.getCookie(url.toString()) ?: return emptyList()
        return rawCookies
            .split(";")
            .mapNotNull { cookie -> Cookie.parse(url, cookie.trim()) }
    }
}
