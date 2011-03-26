package com.example

import unfiltered.request._
import unfiltered.response._
import org.jboss.netty.channel.{Channel,ChannelFuture,ChannelFutureListener}
import org.jboss.netty.channel.group.{DefaultChannelGroup, ChannelGroupFuture}
import org.jboss.netty.handler.codec.http.{HttpHeaders, DefaultHttpChunk,
                                           DefaultHttpChunkTrailer}
import org.jboss.netty.buffer.ChannelBuffers


/** unfiltered plan */
class App extends unfiltered.netty.channel.Plan {
  import QParams._
  
  implicit def block2listener(block: () => Unit) =
    new ChannelFutureListener {
      def operationComplete(future: ChannelFuture) { block() }
    }
  val ChunkedJson =
    unfiltered.response.Connection(HttpHeaders.Values.CLOSE) ~>
    TransferEncoding(HttpHeaders.Values.CHUNKED) ~>
    JsonContent

  def intent = {
    case req @ GET(Path(Seg("nyp" :: Nil))) =>
      val ch = req.underlying.event.getChannel
      val initial = req.underlying.defaultResponse(ChunkedJson)
      ch.write(initial).addListener { () =>
        App.clients.add(ch)
        ()
      }
  }
}

object App {
  val clients = new DefaultChannelGroup
}

/** embedded server */
object Server {
  def main(args: Array[String]) {
    val scraper = new Thread {
      private var plsfinish = false
      def finish() { plsfinish = true }
      override def run() {
        while (!plsfinish) {
          println("writing")
          App.clients.write(new DefaultHttpChunk(
            ChannelBuffers.copiedBuffer("".getBytes("utf-8"))
          ))
          Thread.sleep(5000)
        }
      }
    }
    scraper.start()
    unfiltered.netty.Http(8080)
      .handler(new App)
      .beforeStop {
        scraper.finish()
        scraper.join()
        App.clients.write(new DefaultHttpChunkTrailer).awaitUninterruptibly()
      }
      .run()
  }
}
