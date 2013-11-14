package proxy;

/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.vertx.scala.platform.Verticle
import org.vertx.scala.core.http.HttpServerRequest
import org.vertx.scala.core.http.HttpClientResponse
import org.vertx.scala.core.buffer.Buffer

class ProxyServer extends Verticle {

  override def start() {

    val client = vertx.createHttpClient.setHost("localhost").setPort(8282)

    vertx.createHttpServer.requestHandler({ req: HttpServerRequest =>
      println("Proxying request: " + req.uri())
      val cReq = client.request(req.method(), req.uri(), { cRes: HttpClientResponse =>
        println("Proxying response: " + cRes.statusCode())
        req.response().setStatusCode(cRes.statusCode())
        req.response().headers().set(cRes.headers())
        req.response().setChunked(true)
        cRes.dataHandler({ data: Buffer =>
          println("Proxying response body:" + data)
          req.response().write(data)
        })
        cRes.endHandler({
          req.response().end()
        })
      })
      cReq.headers().set(req.headers())
      cReq.setChunked(true)
      req.dataHandler({ data: Buffer =>
        println("Proxying request body:" + data)
        cReq.write(data)
      })
      req.endHandler({
        println("end of the request")
        cReq.end()
      })
    }).listen(8080)
  }
}