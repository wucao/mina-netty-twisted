# -*- coding:utf-8 –*-

from twisted.internet.protocol import Protocol
from twisted.internet.protocol import Factory
from twisted.internet import reactor

class TcpServerHandle(Protocol):

    # 新的连接建立
    def connectionMade(self):
        print 'connectionMade'

    # 连接断开
    def connectionLost(self, reason):
        print 'connectionLost'

    # 接收到新数据
    def dataReceived(self, data):
        print 'dataReceived', data
        self.transport.write('你好')

factory = Factory()
factory.protocol = TcpServerHandle
reactor.listenTCP(8080, factory)
reactor.run()