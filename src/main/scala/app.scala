package allaboard

import unfiltered.request._
import unfiltered.response._
import org.jboss.netty.channel.{Channel,ChannelFuture,ChannelFutureListener}
import org.jboss.netty.channel.group.{DefaultChannelGroup, ChannelGroupFuture}
import org.jboss.netty.handler.codec.http.{HttpHeaders, DefaultHttpChunk,
                                           DefaultHttpChunkTrailer}
import org.jboss.netty.buffer.ChannelBuffers


/** unfiltered plan */
class App extends unfiltered.netty.channel.Plan {
  implicit def block2listener(block: () => Unit) =
    new ChannelFutureListener {
      def operationComplete(future: ChannelFuture) { block() }
    }

  val ChunkedJson =
    unfiltered.response.Connection(HttpHeaders.Values.CLOSE) ~>
    TransferEncoding(HttpHeaders.Values.CHUNKED) ~>
    JsonContent

  def intent = {
    case req @ GET(Path(Seg("ny" :: Nil))) =>
      val ch = req.underlying.event.getChannel
      val initial = req.underlying.defaultResponse(ChunkedJson)
      ch.write(initial).addListener { () =>
        ch.write(Broadcast.chunk(Polling.departures))
        Broadcast.clients.add(ch)
        ()
      }
  }
}

/** embedded server */
object Server {
  def main(args: Array[String]) {
    Polling.start()
    unfiltered.netty.Http(8080)
      .handler(new App)
      .beforeStop {
        Polling.finish()
        Polling.join()
        Broadcast.clients.write(
          new DefaultHttpChunkTrailer
        ).awaitUninterruptibly()
      }
      .run()
  }
}
