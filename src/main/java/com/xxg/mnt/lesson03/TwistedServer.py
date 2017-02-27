# -*- coding:utf-8 –*-

from twisted.protocols.basic import Int32StringReceiver
from twisted.internet.protocol import Factory
from twisted.internet import reactor

class TcpServerHandle(Int32StringReceiver):

    # 新的连接建立
    def connectionMade(self):
        print 'connectionMade'

    # 连接断开
    def connectionLost(self, reason):
        print 'connectionLost'

    # 接收到新的数据
    def stringReceived(self, data):
        print 'stringReceived:' + data

factory = Factory()
factory.protocol = TcpServerHandle
reactor.listenTCP(8080, factory)
reactor.run()