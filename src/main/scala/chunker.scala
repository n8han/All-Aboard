package allaboard

import org.jboss.netty.channel.Channel
import org.jboss.netty.handler.codec.http.DefaultHttpChunk
import org.jboss.netty.buffer.ChannelBuffers

import com.gent.departurevision.DepartureVision

object Chunker {
  def chunk(departures: Set[DepartureVision.Departure]) = {
    import net.liftweb.json._

    implicit val formats = Serialization.formats(NoTypeHints)
    new DefaultHttpChunk(
      ChannelBuffers.copiedBuffer(
        departures.map(
          Serialization.write(_)
        ).mkString("","\n","\n").getBytes("utf-8")
      )
    )
  }
  def apply(departures: Set[DepartureVision.Departure])
           (block: DefaultHttpChunk => Any) {
    if (!departures.isEmpty) block(chunk(departures))
  }
}
