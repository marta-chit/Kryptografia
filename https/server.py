import BaseHTTPServer
import urlparse
import urllib 
import SocketServer
import threading
import ssl

HOST_NAME = 'localhost'
PORT_NUMBER = 8002


class Handler(BaseHTTPServer.BaseHTTPRequestHandler):
    def do_HEAD(self):
        self.send_response(200)
        self.send_header("Content-type", "text/html")
        self.end_headers()

    def do_GET(self):

        self.send_response(200)
        self.send_header("Content-type", "text/html")
        self.end_headers()

        if self.path == '/get/':
            self.wfile.write('On /get/')
            return
        self.wfile.write('Hello')
        return    

    def do_POST(self):
        pass

class ThreadedHTTPServer(SocketServer.ThreadingMixIn, BaseHTTPServer.HTTPServer):
    pass


if __name__ == '__main__':
    httpd = BaseHTTPServer.HTTPServer((HOST_NAME, PORT_NUMBER), Handler)
    httpd.socket = ssl.wrap_socket (httpd.socket, keyfile='privkeyA.pem', certfile='certA.crt', server_side=True)
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        httpd.server_close()
    print "Server Stopped - %s:%s" % (HOST_NAME, PORT_NUMBER)
