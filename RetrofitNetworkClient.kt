

import android.text.TextUtils
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.*


/**
 * @author Muhammad Atif
 * @@Link GitHub.com/AtifAbbasi19
 */

class RetrofitNetworkClient(retrofitBuilder: Retrofit.Builder) {


    var genericNetworkService: GenericNetworkService? = null

    init {
        val retrofit = retrofitBuilder.build() // initializing client to get access of network
        genericNetworkService = retrofit.create(GenericNetworkService::class.java) // initializing service to get access of end point
    }

//     constructor(builder: Builder) : this(builder)

    /**
     * using builder pattren here for generic network Interceptor
     * static class by default for builder pattren
     */

    class Builder {

        var baseURL: String? = BuildConfig.BASE_URL    //default base /Host Url
            private set //setter method for BASE URL/Host URL

        var connectTimeoutSeconds: Long = 60 //default time out
            private set //setter method for BASE URL/Host URL

        var readTimeoutSeconds: Long = 60   //default time out
            private set //setter method for BASE URL/Host URL

        var writeTimeoutSeconds: Long = 60 //default time out
            private set //setter method for BASE URL/Host URL

        var retryOnConnectionFailure = true //default retry flag
            private set //setter method for BASE URL/Host URL

        var hostnameVerifier = true //default retry flag
            private set //setter method for BASE URL/Host URL

        var sslFactory: SSLSocketFactory? = null  //socket factory for certificate
            private set //setter method for BASE URL/Host URL

        var interceptor: Interceptor? = null  //socket factory for certificate
            private set //setter method for BASE URL/Host URL

        var trustManagerFactory: TrustManagerFactory? = null  // trust manager
            private set //setter method for BASE URL/Host URL

        var x509TrustManager: X509TrustManager? = null  // trust manager
            private set //setter method for BASE URL/Host URL


        var addRequestHeadersAuthorization: String? = null  // trust manager
            private set //setter method for BASE URL/Host URL


        fun baseURL(baseURL: String) = apply { this.baseURL = baseURL }  //base /host url

        fun setSSlSocketFactory(sslFactory: SSLSocketFactory) {
            this.sslFactory = sslFactory
        }

        fun setSSlSocketFactory(interceptor: Interceptor) {
            this.interceptor = interceptor
        }

        fun setHostnameVerifier(hostnameVerifier: Boolean) {
            this.hostnameVerifier = hostnameVerifier
        }


        fun addRequestHeadersAuthorization(addRequestHeadersAuthorization: String) = apply { this.addRequestHeadersAuthorization = addRequestHeadersAuthorization }  //base /host url


        /**
         * https://stackoverflow.com/questions/31002159/now-that-sslsocketfactory-is-deprecated-on-android-what-would-be-the-best-way-t
         */
        fun setX509TrustManager(x509TrustManager: X509TrustManager) {
            this.x509TrustManager = x509TrustManager
        }

        fun setTrustManagerFactory(trustManagerFactory: TrustManagerFactory) {
            this.trustManagerFactory = trustManagerFactory
        }

        fun readTimeoutSeconds(readTimeoutSeconds: Long) = apply { this.readTimeoutSeconds = readTimeoutSeconds }

        fun connectTimeoutSeconds(connectTimeoutSeconds: Long) = apply { this.connectTimeoutSeconds = connectTimeoutSeconds }

        fun writeTimeoutSeconds(writeTimeoutSeconds: Long) = apply { this.writeTimeoutSeconds = writeTimeoutSeconds }

        fun retryOnConnectionFailure(retryOnConnectionFailure: Boolean) = apply { this.retryOnConnectionFailure = retryOnConnectionFailure }


        fun generateRetrofitBuilder(): Retrofit.Builder {


            val builder = OkHttpClient.Builder()
                    .connectTimeout(connectTimeoutSeconds, TimeUnit.SECONDS)
                    .readTimeout(readTimeoutSeconds, TimeUnit.SECONDS)
                    .writeTimeout(writeTimeoutSeconds, TimeUnit.SECONDS)
                    .addInterceptor(ResponseLogInterceptor())
                    .addInterceptor(interceptor ?: object : Interceptor {
                        override fun intercept(chain: Interceptor.Chain): Response {

                            /**
                             * todo will update this section into generic header
                             */

                            val original = chain.request()

                            // Request customization: add request headers
                            val requestBuilder = original.newBuilder()
                            if (!TextUtils.isEmpty(addRequestHeadersAuthorization))
                                requestBuilder.header("Authorization", addRequestHeadersAuthorization!!) // <-- this is the important line

                            val request = requestBuilder       //add Headers
                                    // .addHeader("Content-Type", "application/json")
                                    .addHeader("client", "android")
                                    .addHeader("language", Locale.getDefault().language)
                                    .addHeader("os", android.os.Build.VERSION.RELEASE)
                                    // .addHeader("x-bb-client-key", "token")
                                    .build()

                            return chain.proceed(request)

                        }

                    })
                    .retryOnConnectionFailure(retryOnConnectionFailure)
                    .hostnameVerifier(object : HostnameVerifier {
                        override fun verify(hostname: String?, session: SSLSession?): Boolean {
                            return hostnameVerifier
                        }
                    })





            if (BuildConfig.DEBUG)
                builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

            if (null != sslFactory && null != x509TrustManager) {
                builder.sslSocketFactory(sslFactory!!, x509TrustManager!!)
            } else {
                if (null != sslFactory) {
                    builder.sslSocketFactory(sslFactory!!)
                }
            }


            val okHttpClient = builder.build()

            return Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(baseURL ?: BuildConfig.BASE_URL)
//                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .addConverterFactory(GsonConverterFactory.create())
        }


        fun build() = RetrofitNetworkClient(generateRetrofitBuilder())
    }


}