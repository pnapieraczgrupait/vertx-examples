package wsperf;

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
import org.vertx.scala.core.http.WebSocket

class ConnectClient extends Verticle {

  // Number of connections to create
  private val CONNS = 1000
  var connectCount = 0

  override def start() {
    println("Starting perf client");
    val client = vertx.createHttpClient().setPort(8080).setHost("localhost").setMaxPoolSize(CONNS)
    for (i <- 0 until CONNS) {
      println("connecting ws");
      client.connectWebsocket("/someuri", { ws: WebSocket =>
        connectCount += 1
        println("ws connected: " + connectCount)
      })
    }
  }
}