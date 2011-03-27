package allaboard

import org.jboss.netty.channel.group.{DefaultChannelGroup, ChannelGroupFuture}
import org.jboss.netty.handler.codec.http.DefaultHttpChunk
import org.jboss.netty.buffer.ChannelBuffers

import com.gent.departurevision.DepartureVision

object Broadcast {
  val clients = new DefaultChannelGroup

  def chunk(departures: Set[DepartureVision.Departure]) =
    new DefaultHttpChunk(
      ChannelBuffers.copiedBuffer(
        (departures.toString + "\n").getBytes("utf-8")
      )
    )

  def write(departures: Set[DepartureVision.Departure]) {
    if (!departures.isEmpty) clients.write(chunk(departures))
  }
}
