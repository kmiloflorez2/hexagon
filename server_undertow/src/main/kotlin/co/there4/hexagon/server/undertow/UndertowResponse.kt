package co.there4.hexagon.server.undertow

import io.undertow.server.HttpServerExchange
import co.there4.hexagon.server.EngineResponse
import io.undertow.util.HttpString
import java.io.OutputStream
import java.net.HttpCookie

class UndertowResponse(exchange: HttpServerExchange) : EngineResponse {
    override var contentType: String?
        get() = throw UnsupportedOperationException()
        set(value) {
        }

    override val outputStream: OutputStream
        get() = throw UnsupportedOperationException()

    override fun getMimeType(file: String): String? {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addHeader(name: String, value: String) {
        e.responseHeaders.put(HttpString(name), value)
    }

    override fun addCookie(cookie: HttpCookie) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeCookie(name: String) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val e: HttpServerExchange = exchange

    override var body: Any = ""
        get() = field
        set(value) { field = value }
    override var status: Int
        get() = e.statusCode
        set(value) { e.statusCode = value }

    override fun redirect(url: String) {
        throw UnsupportedOperationException()
    }
}
