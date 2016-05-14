package app.bitrader.api.network

import java.nio.charset.Charset
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import android.content.Context
import app.bitrader.LocalProperties
import com.squareup.okhttp.Interceptor.Chain
import com.squareup.okhttp._
import okio.{Buffer, BufferedSink}

/**
  * Created by Alex Afanasev
  */
class AuthInterceptor(ctx: Context) extends Interceptor {

  override def intercept(chain: Chain): Response = {
    val request: Request = chain.request()
    val newRequest = request.newBuilder

    val query: String = { // ugly, ugly function
      val buffer: Buffer = new okio.Buffer()
      request.body().writeTo(buffer)
      val postString: String = buffer.readString(Charset.forName("UTF-8"))
      postString
    }

    println(s"post query $query")

    // todo put nonce here
//    val formEncodingBuilder: FormEncodingBuilder = new FormEncodingBuilder
//    formEncodingBuilder.add()
//    newRequest.post(formEncodingBuilder)

    newRequest.addHeader("Key", LocalProperties.apiKey)
    newRequest.addHeader("Sign", hmacSHA512(query))

    val build: Request = newRequest.build()
    chain.proceed(build)
  }

  def hmacSHA512(value: String): String = {
    val hmacsha512: String = "HmacSHA512"
    val encoding: String = "UTF-8"
    val secretKeySpec: SecretKeySpec = new SecretKeySpec(LocalProperties.apiSecret.getBytes(encoding), hmacsha512)
    val mac: Mac = Mac.getInstance(hmacsha512)
    mac.init(secretKeySpec)
    val bytes: Array[Byte] = mac.doFinal(value.getBytes(encoding))

    bytes.map("%02x" format _).mkString
  }

}
