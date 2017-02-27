# -*- coding:utf-8 –*-

from twisted.protocols.basic import LineOnlyReceiver
from twisted.internet.protocol import Factory
from twisted.internet import reactor

class TcpServerHandle(LineOnlyReceiver):

    # 新的连接建立
    def connectionMade(self):
        print 'connectionMade'

    # 连接断开
    def connectionLost(self, reason):
        print 'connectionLost'

    # 接收到新的一行数据
    def lineReceived(self, data):
        print 'lineReceived:' + data

factory = Factory()
factory.protocol = TcpServerHandle
reactor.listenTCP(8080, factory)
reactor.run()