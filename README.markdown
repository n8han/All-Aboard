This is a streaming server that lazily polls train status services and
broadcasts updates out to any listeners. To try it, you'll need to
have [sbt installed][sbt]. Then, form the project directory:

[sbt]: http://code.google.com/p/simple-build-tool/wiki/Setup

    $ sbt update run

This will display a message if the server starts successfully. In a
second terminal window:

    curl -i http://technically.us:7979/njt/ny

You should see NJ Transit and Amtrak departures from Penn Station,
with changes (delays, boarding, etc) streamed out within ten seconds
of them being posted to NJ Transit's departures page.
