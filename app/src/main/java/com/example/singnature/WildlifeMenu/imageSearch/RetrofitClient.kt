package com.example.singnature.WildlifeMenu.imageSearch

import android.content.Context
import com.example.singnature.R
import com.example.singnature.WildlifeMenu.WildlifeFragment
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object RetrofitClient {
    //    private const val BASE_URL = "https://oyster-app-bkrkc.ondigitalocean.app/"
    private const val BASE_URL = "https://167.172.73.161/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val instance : ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    fun getUnsafeOkHttpClient(context: Context) : OkHttpClient {
        try {
            // Load self-signed certificate from res/raw/my_cert.cer
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val inputStream: InputStream = context.resources.openRawResource(R.raw.cloud_cert)
            val certificate = certificateFactory.generateCertificate(inputStream)
            inputStream.close()

            // Create a KeyStore with our trusted certificate
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
                load(null, null)
                setCertificateEntry("custom_cert", certificate)
            }

            // Initialize TrustManager with KeyStore
            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
                init(keyStore)
            }

            // Create an SSLContext with our custom TrustManager
            val sslContext = SSLContext.getInstance("TLS").apply {
                init(null, trustManagerFactory.trustManagers, null)
            }

            // Create OkHttpClient with custom SSL settings
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            return OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustManagerFactory.trustManagers[0] as X509TrustManager)
                .hostnameVerifier{ _, _ -> true}
                .addInterceptor(loggingInterceptor)
                .build()
        } catch (e: Exception) {
            throw RuntimeException("Failed to create SSL connection", e)
        }
    }

    fun getInstance(context: Context): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getUnsafeOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
