package co.there4.hexagon.server

import java.net.HttpCookie

/**
 * Lists would be initialized loading all elements when they are used (set it as lazy in
 * implementations) this will have a performace penalty in favor of ease of use. The alternative
 * would be using a 'Map/List wrapper that delegates calls to abstract methods in the interface
 * (I won't do this just now).
 *
 * HTTP request context. It holds client supplied data and methods to change the response.
 */
class Request(private val request: EngineRequest) {
    val method: HttpMethod by lazy { request.method }
    val scheme: String by lazy { request.scheme }
    val host: String by lazy { request.host }
    val port: Int by lazy { request.port }
    val path: String by lazy { request.path }
    val queryString: String by lazy { request.queryString }
    val body: String by lazy { request.body }

    val contentLength: Long by lazy { request.contentLength }
    val contentType: String? by lazy { request.contentType }
    val userAgent: String by lazy { request.userAgent }
    val url: String by lazy { request.url }
    val ip: String by lazy { request.ip }
    val referrer: String by lazy { request.referrer }
    val secure: Boolean by lazy { request.secure }
    val forwarded: Boolean by lazy { request.forwarded }
    val xhr: Boolean by lazy { request.xhr }
    val preferredType: String by lazy { request.preferredType }

    val parameters: Map<String, List<String>> by lazy { request.parameters }
    val headers: Map<String, List<String>> by lazy { request.headers }
    val cookies: Map<String, HttpCookie> by lazy { request.cookies }
    val parts: Map<String, Part> by lazy { request.parts }

    operator fun get(name: String): String? = parameters[name]?.first()

    fun accept () = headers["Accept"]

    fun parameter(name: String) = get(name) ?: error ("'$name' parameter not found")
}
