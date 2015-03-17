package com.stripe

import java.net.URLEncoder
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.util.Properties
import scala.util.matching.Regex

import org.apache.commons.codec.binary.Base64
import org.apache.http.client._
import org.apache.http.client.entity._
import org.apache.http.client.methods._
import org.apache.http.client.params._
import org.apache.http.impl.client._
import org.apache.http.message._
import org.apache.http.params._
import org.apache.http.util._

import net.liftweb.json
import net.liftweb.json.JsonDSL._

sealed abstract class StripeException(msg: String, cause: Throwable = null) extends Exception(msg, cause)
case class APIException(msg: String, cause: Throwable = null) extends StripeException(msg, cause)
case class APIConnectionException(msg: String, cause: Throwable = null) extends StripeException(msg, cause)
case class CardException(msg: String, code: Option[String] = None, param: Option[String] = None) extends StripeException(msg)
case class InvalidRequestException(msg: String, param: Option[String] = None) extends StripeException(msg)
case class AuthenticationException(msg: String) extends StripeException(msg)

//represents Errors returned as JSON
case class ErrorContainer(error: Error)
case class Error(`type`: String, message: String, code: Option[String], param: Option[String])

abstract class APIResource {
  val ApiBase = "https://api.stripe.com/v1"
  val BindingsVersion = "1.1.2"
  val CharSet = "UTF-8"

  //lift-json format initialization
  implicit val formats = json.DefaultFormats

  //utility methods
  def base64(in: String) = new String(Base64.encodeBase64(in.getBytes(CharSet)))
  def urlEncodePair(k:String, v: String) = "%s=%s".format(URLEncoder.encode(k, CharSet), URLEncoder.encode(v, CharSet))
  val className = this.getClass.getSimpleName.toLowerCase.replace("$","")
  val classURL = "%s/%ss".format(ApiBase, className)
  def instanceURL(id: String) = "%s/%s".format(classURL, id)
  val singleInstanceURL = "%s/%s".format(ApiBase, className)

  /*
      We want POST vars of form:
      {'foo': 'bar', 'nested': {'a': 'b', 'c': 'd'}}
      to become:
      foo=bar&nested[a]=b&nested[c]=d
  */
  def flattenParam(k: String, v: Any): List[(String, String)] = {
    v match {
      case None => Nil
      case m: Map[_,_] => m.flatMap(kv => flattenParam("%s[%s]".format(k,kv._1), kv._2)).toList
      case _ => List((k,v.toString))
    }
  }

  def httpClient: DefaultHttpClient = {
    if (apiKey == null || apiKey.isEmpty) {
      throw AuthenticationException("No API key provided. (HINT: set your API key using 'stripe.apiKey = <API-KEY>'. You can generate API keys from the Stripe web interface. See https://stripe.com/api for details or email support@stripe.com if you have questions.")
    }

    //debug headers
    val javaPropNames = List("os.name", "os.version", "os.arch", "java.version", "java.vendor", "java.vm.version", "java.vm.vendor")
    val javaPropMap = javaPropNames.map(n => (n.toString, Properties.propOrEmpty(n).toString)).toMap
    val fullPropMap = javaPropMap + (
      "scala.version" -> Properties.scalaPropOrEmpty("version.number"),
      "bindings.version" -> BindingsVersion,
      "lang" -> "scala",
      "publisher" -> "stripe"
    )

    val defaultHeaders = asJavaCollection(List(
      new BasicHeader("X-Stripe-Client-User-Agent", json.compact(json.render(fullPropMap))),
      new BasicHeader("User-Agent", "Stripe/v1 ScalaBindings/%s".format(BindingsVersion)),
      new BasicHeader("Authorization", "Bearer %s".format(apiKey))
    ))

    val httpParams = new SyncBasicHttpParams().
      setParameter(ClientPNames.DEFAULT_HEADERS, defaultHeaders).
      setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,CharSet).
      setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,30000). //30 seconds
      setParameter(CoreConnectionPNames.SO_TIMEOUT,80000) //80 seconds

    new DefaultHttpClient(connectionManager, httpParams)
  }

  def getRequest(url: String, paramList: List[(String,String)]): HttpRequestBase = {
    new HttpGet("%s?%s".format(url, paramList.map(kv => urlEncodePair(kv._1, kv._2)).mkString("&")))
  }

  def deleteRequest(url: String, paramList: List[(String, String)]): HttpRequestBase = {
    val request = new HttpDeleteWithBody(url)
    val deleteParamList = paramList.map(kv => new BasicNameValuePair(kv._1, kv._2))
    request.setEntity(new UrlEncodedFormEntity(seqAsJavaList(deleteParamList), CharSet))
    request
  }

  def postRequest(url: String, paramList: List[(String, String)]): HttpRequestBase = {
    val request = new HttpPost(url)
    val postParamList = paramList.map(kv => new BasicNameValuePair(kv._1, kv._2))
    request.setEntity(new UrlEncodedFormEntity(seqAsJavaList(postParamList), CharSet))
    request
  }

  def rawRequest(method: String, url: String, params: Map[String,_] = Map.empty): (String, Int) = {
    val client = httpClient
    val paramList = params.flatMap(kv => flattenParam(kv._1, kv._2)).toList
    try {
      val request = method.toLowerCase match {
        case "get" => getRequest(url, paramList)
        case "delete" => deleteRequest(url, paramList)
        case "post" => postRequest(url, paramList)
        case _ => throw new APIConnectionException("Unrecognized HTTP method %r. This may indicate a bug in the Stripe bindings. Please contact support@stripe.com for assistance.".format(method))
      }
      val response = client.execute(request)
      val entity = response.getEntity
      val body = EntityUtils.toString(entity)
      EntityUtils.consume(entity)
      (body, response.getStatusLine.getStatusCode)
    } catch {
      case e @ (_: java.io.IOException | _: ClientProtocolException) => throw APIConnectionException("Could not connect to Stripe (%s). Please check your internet connection and try again. If this problem persists, you should check Stripe's service status at https://twitter.com/stripe, or let us know at support@stripe.com.".format(ApiBase), e)
    } finally {
      client.getConnectionManager.shutdown()
    }
  }

  def request(method: String, url: String, params: Map[String,_] = Map.empty): json.JValue = {
    val (rBody, rCode) = rawRequest(method, url, params)
    interpretResponse(rBody, rCode)
  }

  val CamelCaseRegex = new Regex("(_.)")

  def interpretResponse(rBody: String, rCode: Int): json.JValue = {
    val jsonAST = json.parse(rBody).transform {
      //converts json camel_case field names to Scala camelCase field names
      case json.JField(fieldName, x) => json.JField(CamelCaseRegex.replaceAllIn(
        fieldName, (m: Regex.Match) => m.matched.substring(1).toUpperCase), x)
    }
    if (rCode < 200 || rCode >= 300) handleAPIError(rBody, rCode, jsonAST)
    jsonAST
  }

  def handleAPIError(rBody: String, rCode: Int, jsonAST: json.JValue) {
    val error = try {
       jsonAST.extract[ErrorContainer].error
    } catch {
      case e: json.MappingException => throw new APIException(
        "Unable to parse response body from API: %s (HTTP response code was %s)".format(rBody, rCode), e)
    }
    rCode match {
      case (400 | 404) => throw new InvalidRequestException(error.message, param=error.param)
      case 401 => throw new AuthenticationException(error.message)
      case 402 => throw new CardException(error.message, code=error.code, param=error.param)
      case _ => throw new APIException(error.message, null)
    }
  }
}
