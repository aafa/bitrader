package app.bitrader.api

import java.net.URL
import java.util.Date

import json.{JSONAccessor, JValue}
import okhttp3.HttpUrl.Builder
import okhttp3._

/**
  * Created by Alex Afanasev
  */
abstract class AbstractApi(url: String) extends Api{
  lazy val httpClient = new OkHttpClient()

  val baseUrl: HttpUrl = HttpUrl.get(new URL(url))

  def execute(request1: Request): Response = httpClient.newCall(request1).execute()
  def nonce: String = new Date().getTime.toString

  // todo append path params
  def get[Result : JSONAccessor](params: Map[String, String], appendUrl: String = ""): Result = {
    val reqBuilder: Builder = baseUrl.newBuilder()

    for ((k,v) <- params){
      reqBuilder.addEncodedQueryParameter(k, v)
    }

    reqBuilder.addEncodedQueryParameter("nonce", nonce)

    println("request: " + reqBuilder.toString)

    val r: Request = new Request.Builder().url(reqBuilder.build()).get().build()
    val response: Response = execute(r)
    val respString: String = response.body().string()

    println("response: " + respString)
    JValue.fromString(respString).toObject[Result]
  }


  def post[Result : JSONAccessor](params: Map[String, String]): Result = {
    val rb = new FormBody.Builder()

    for ((k,v) <- params){
      rb.add(k, v)
    }

    rb.add("nonce", nonce)

    val r: Request = new Request.Builder().url(baseUrl).post(rb.build()).build()
    val response: Response = execute(r)
    val respString: String = response.body().string()

    JValue.fromString(respString).toObject[Result]
  }
}
