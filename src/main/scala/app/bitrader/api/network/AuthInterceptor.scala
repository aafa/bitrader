package app.bitrader.api.network

import java.nio.charset.Charset
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import app.bitrader.activity.Circuitable
import app.bitrader.api.common.AuthData
import okhttp3.Interceptor.Chain
import okhttp3.{Interceptor, Request, Response}
import okio.Buffer

/**
  * Created by Alex Afanasev
  */
class AuthInterceptor extends Interceptor with Circuitable{

  def credentials: Option[AuthData] =  appCircuit.zoom(_.selectedAccount).value.context.auth.authData

  override def intercept(chain: Chain): Response = {
    credentials.map {cc =>
      val request: Request = chain.request()
      val newRequest = request.newBuilder

      val query: String = {  // ugly, ugly function
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

      newRequest.addHeader("Key", cc.apiKey)
      newRequest.addHeader("Sign", hmacSHA512(query, cc.apiSecret))

      val build: Request = newRequest.build()
      chain.proceed(build)
    }.getOrElse(
      chain.proceed(chain.request())
    )
  }

  def hmacSHA512(value: String, secret: String): String = {
    val hmacsha512: String = "HmacSHA512"
    val encoding: String = "UTF-8"
    val secretKeySpec: SecretKeySpec = new SecretKeySpec(secret.getBytes(encoding), hmacsha512)
    val mac: Mac = Mac.getInstance(hmacsha512)
    mac.init(secretKeySpec)
    val bytes: Array[Byte] = mac.doFinal(value.getBytes(encoding))

    bytes.map("%02x" format _).mkString
  }

}
