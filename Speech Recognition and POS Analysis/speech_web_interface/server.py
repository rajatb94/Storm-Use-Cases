import tornado.ioloop
import tornado.web
import tornado.websocket
import json

clients = []
clients2 = []

class WebSocketChatHandler(tornado.websocket.WebSocketHandler):
  def open(self, *args):
    clients.append(self)
    print "new Client", self

  def on_message(self, message):        
    print message
    j = json.loads(message)

    if j["client"]!="bolt":
      for client in clients:
      
        if(client!=self):
          print self, "    ",client
          client.write_message(j['data'])   
          
    else:
      for client in clients:
      
        if client==self or client ==clients[0]:
          a=2
        else:
          print self, "    ",client
          client.write_message(j['data'])  
        

        
  def on_close(self):
  	clients.remove(self)

app = tornado.web.Application([(r'/said', WebSocketChatHandler)])

app.listen(9999)
tornado.ioloop.IOLoop.instance().start()