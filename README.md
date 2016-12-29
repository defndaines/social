# Social

Simple RESTful web service [kata](http://codekata.com/) using
[Pedestal](https://github.com/pedestal/pedestal) and Clojure.

The service implements a small social service, allowing users to post messages.
Other users can then like or comment on existing messages. Users can look up
messages by which ones were posted most recently or which ones are most popular.
The possibility of location-based searching was also laid out, but not all
endpoints were implemented within the time constraints.

The service stores all messages in-memory as an
[atom](http://clojure.org/reference/atoms). This means that stopping the service
loses all messages, and restarting creates a fresh instance.

## API

Enumeration of the endpoints supported by this service. At this time, not all of
these endpoints are active.

### Home Page, `GET /`

Just returns the string "Message!".

```
curl 'http://localhost:8080'
```

### List All Messages, `GET /messages`

Get a list of all the messages which have been posted into the service.

```
curl 'http://localhost:8080/messages
```

### Create a New Message, `POST /messages`

### Get Individual Message, `GET /messages/:id`

Get details of an individual message by its ID.

### "Like" a Message, `PUT /messages/:id`

### Show Replies to a Message, `GET /messages/:id/replies`

### List the Latest Messages, `GET /messages/latest`

### List the Most Popular Messages, `GET /messages/popular`

### List Nearby Messages, `GET /messages/nearby`

## History

I created this as a one-day (about 5–6 hours) kata which was conceived as a
possible hiring challenge. For me, it was a crash course in Pedestal, as I only
knew of Pedestal's existence prior to diving into this project. As a result, I
spent a good hour or so just reading documentation before coding. I was working
against a list of requirements, which is why a number of endpoints are stubbed
out but not implemented.

Prior to this challenge, I had never implemented a RESTful service from scratch.
I had worked on a number of preexisting services, but the API decisions
had been laid out already for those.
