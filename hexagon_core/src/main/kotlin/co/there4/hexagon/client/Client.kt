package co.there4.hexagon.client

import co.there4.hexagon.serialization.serialize
import co.there4.hexagon.server.HttpMethod
import co.there4.hexagon.server.HttpMethod.*
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import org.asynchttpclient.DefaultAsyncHttpClient
import org.asynchttpclient.DefaultAsyncHttpClientConfig.Builder
import org.asynchttpclient.Response
import org.asynchttpclient.cookie.Cookie
import java.io.File
import java.net.URL
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import java.util.Base64.Encoder
import org.asynchttpclient.request.body.multipart.Part as AsyncHttpPart

/**
 * Client to use other REST services.
 */
class Client (
    val endpointUrl: URL,
    val contentType: String? = null,
    val useCookies: Boolean = true,
    user: String? = null,
    password: String? = null,
    insecure: Boolean = false) {

    val base64encoder: Encoder = Base64.getEncoder()
    val authorization: String? =
        if (user != null)
        base64encoder.encodeToString("$user:$password".toByteArray(UTF_8))
        else null

    val endpoint = endpointUrl.toString()
    val client = DefaultAsyncHttpClient(Builder()
        .setConnectTimeout(5000)
        .setSslContext(
            if (insecure)
                SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build()
            else SslContextBuilder.forClient().build()
        )
        .build())
    val cookies: MutableMap<String, Cookie> = mutableMapOf()

    constructor(endpointUrl: String, contentType: String? = null, useCookies: Boolean = true):
        this(URL(endpointUrl), contentType, useCookies)

    /**
     * Synchronous execution.
     */
    fun send (
        method: HttpMethod,
        url: String = "/",
        body: String? = null,
        contentType: String? = this.contentType): Response {

        val request = createRequest(method, url, contentType)

        if (body != null)
            request.setBody(body)

        if (useCookies)
            cookies.forEach { request.addCookie(it.value) }

        val response = request.execute().get()

        if (useCookies) {
            response.cookies.forEach {
                if (it.value == "")
                    cookies.remove(it.name)
                else
                    cookies[it.name] = it
            }
        }

        return response
    }

    fun sendObject(
        method: HttpMethod,
        url: String,
        contentType: String = requireContentType(),
        body: Any) = when (body) {
            is File -> send(method, url, Base64.getEncoder().encodeToString(body.readBytes()), contentType)
            else -> send(method, url, body.serialize(contentType), contentType)
        }

    private fun requireContentType() = this.contentType ?: error("Missing content type")

    fun get (url: String, body: String? = null) = send (GET, url, body)
    fun head (url: String, body: String? = null) = send (HEAD, url, body)
    fun post (url: String, body: String? = null) = send (POST, url, body)
    fun put (url: String, body: String? = null) = send (PUT, url, body)
    fun delete (url: String, body: String? = null) = send (DELETE, url, body)
    fun trace (url: String, body: String? = null) = send (TRACE, url, body)
    fun options (url: String, body: String? = null) = send (OPTIONS, url, body)
    fun patch (url: String, body: String? = null) = send (PATCH, url, body)

    fun get (url: String, contentType: String = requireContentType(), body: Any) =
        sendObject(GET, url, contentType, body)

    fun get (url: String, contentType: String = requireContentType(), body: () -> Any) =
        get(url, contentType, body())

    fun head (url: String, contentType: String = requireContentType(), body: Any) =
        sendObject(HEAD, url, contentType, body)

    fun head (url: String, contentType: String = requireContentType(), body: () -> Any) =
        head(url, contentType, body())

    fun post (url: String, contentType: String = requireContentType(), body: Any) =
        sendObject(POST, url, contentType, body)

    fun post (url: String, contentType: String = requireContentType(), body: () -> Any) =
        post(url, contentType, body())

    fun put (url: String, contentType: String = requireContentType(), body: Any) =
        sendObject(PUT, url, contentType, body)

    fun put (url: String, contentType: String = requireContentType(), body: () -> Any) =
        put(url, contentType, body())

    fun delete (url: String, contentType: String = requireContentType(), body: Any) =
        sendObject(DELETE, url, contentType, body)

    fun delete (url: String, contentType: String = requireContentType(), body: () -> Any) =
        delete(url, contentType, body())

    fun trace (url: String, contentType: String = requireContentType(), body: Any) =
        sendObject(TRACE, url, contentType, body)

    fun trace (url: String, contentType: String = requireContentType(), body: () -> Any) =
        trace(url, contentType, body())

    fun options (url: String, contentType: String = requireContentType(), body: Any) =
        sendObject(OPTIONS, url, contentType, body)

    fun options (url: String, contentType: String = requireContentType(), body: () -> Any) =
        options(url, contentType, body())

    fun patch (url: String, contentType: String = requireContentType(), body: Any) =
        sendObject(PATCH, url, contentType, body)

    fun patch (url: String, contentType: String = requireContentType(), body: () -> Any) =
        patch(url, contentType, body())

    private fun createRequest(method: HttpMethod, url: String, contentType: String? = null) =
        (endpoint + url).let {
            val request = when (method) {
                GET -> client.prepareGet (it)
                HEAD -> client.prepareHead (it)
                POST -> client.preparePost (it)
                PUT -> client.preparePut (it)
                DELETE -> client.prepareDelete (it)
                TRACE -> client.prepareTrace (it)
                OPTIONS -> client.prepareOptions (it)
                PATCH -> client.preparePatch (it)
            }

            request.setCharset(Charset.defaultCharset()) // TODO Problem if encoding is set?

            if (contentType != null)
                request.addHeader("Content-Type", contentType)

            if (authorization != null)
                request.addHeader("Authorization", "Basic $authorization")

            request
        }
}
