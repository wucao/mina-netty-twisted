# -*- coding:utf-8 –*-

from twisted.protocols.basic import LineOnlyReceiver
from twisted.internet.protocol import Factory
from twisted.internet import reactor, ssl

sslContext = ssl.DefaultOpenSSLContextFactory(
    '/Users/wucao/Desktop/ssl/private.pem',  # 私钥
    '/Users/wucao/Desktop/ssl/cert.crt',  # 证书
)

class TcpServerHandle(LineOnlyReceiver):

    def connectionMade(self):
        print 'connectionMade'

    def connectionLost(self, reason):
        print 'connectionLost'

    def lineReceived(self, data):
        print 'lineReceived:' + data

factory = Factory()
factory.protocol = TcpServerHandle
reactor.listenSSL(8080, factory, sslContext)
reactor.run()