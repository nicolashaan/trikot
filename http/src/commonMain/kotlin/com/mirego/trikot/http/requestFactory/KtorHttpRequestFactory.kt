package com.mirego.trikot.http.requestFactory

import com.mirego.trikot.http.HttpRequest
import com.mirego.trikot.http.HttpRequestFactory
import com.mirego.trikot.http.HttpResponse
import com.mirego.trikot.http.RequestBuilder
import com.mirego.trikot.streams.cancellable.CancellableManager
import com.mirego.trikot.streams.reactive.PublisherFactory
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.reactivestreams.Publisher
import kotlin.coroutines.CoroutineContext

class KtorHttpRequestFactory : HttpRequestFactory {
    override fun request(requestBuilder: RequestBuilder): HttpRequest {
        return KTorCoreHttpRequest(requestBuilder)
    }

    class KTorCoreHttpRequest(val requestBuilder: RequestBuilder) : HttpRequest, CoroutineScope {
        override val coroutineContext: CoroutineContext = Dispatchers.Unconfined

        override fun execute(cancellableManager: CancellableManager): Publisher<HttpResponse> {
            val publisher = PublisherFactory.create<HttpResponse>()

            launch {
                val client = HttpClient()
                try {
                    val response = client.request<String> {
                        url(requestBuilder.baseUrl + requestBuilder.path)
                        requestBuilder.headers.filter { it.key != com.mirego.trikot.http.ContentType }.forEach { entry ->
                            header(entry.key, entry.value)
                        }
                        requestBuilder.body?.let { body = TextContent(it as String, ContentType.Application.Json) }
                        method = requestBuilder.method.ktorMethod
                    }

                    publisher.value = KTorHttpResponse(response)
                } catch (ex: Exception) {
                    publisher.error = ex
                }
            }

            return publisher
        }
    }

    class KTorHttpResponse(value: String) : HttpResponse {
        override val statusCode: Int = 200
        override val bodyString: String? = value
        override val headers: Map<String, String> = HashMap()
        override val source: HttpResponse.ResponseSource = HttpResponse.ResponseSource.UNKNOWN
    }
}

val com.mirego.trikot.http.HttpMethod.ktorMethod: HttpMethod
    get() = when (this) {
        com.mirego.trikot.http.HttpMethod.GET -> HttpMethod.Get
        com.mirego.trikot.http.HttpMethod.DELETE -> HttpMethod.Delete
        com.mirego.trikot.http.HttpMethod.HEAD -> HttpMethod.Head
        com.mirego.trikot.http.HttpMethod.PATCH -> HttpMethod.Patch
        com.mirego.trikot.http.HttpMethod.POST -> HttpMethod.Post
        com.mirego.trikot.http.HttpMethod.PUT -> HttpMethod.Put
    }
