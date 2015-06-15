import tornado.ioloop
import tornado.web
import tornado.websocket

clients = []

class IndexHandler(tornado.web.RequestHandler):
  @tornado.web.asynchronous
  def get(request):
    request.render("index.html")

class WebSocketChatHandler(tornado.websocket.WebSocketHandler):
  def open(self, *args):
    clients.append(self)

  def on_message(self, message):        
    print message
    for client in clients:
  		client.write_message(message)
        
  def on_close(self):
  	clients.remove(self)

app = tornado.web.Application([(r'/said', WebSocketChatHandler), (r'/', IndexHandler)])

app.listen(9999)
tornado.ioloop.IOLoop.instance().start()