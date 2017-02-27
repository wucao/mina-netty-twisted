# -*- coding:utf-8 –*-

from twisted.protocols.basic import LineOnlyReceiver
from twisted.internet.protocol import Factory
from twisted.internet import reactor

class TcpServerHandle(LineOnlyReceiver):

    def __init__(self, factory):
        self.factory = factory

    def connectionMade(self):
        self.factory.clients.add(self) # 新连接添加连接对应的Protocol实例到clients

    def connectionLost(self, reason):
        self.factory.clients.remove(self) # 连接断开移除连接对应的Protocol实例

    def lineReceived(self, line):
        # 遍历所有的连接，发送数据
        for c in self.factory.clients:
            c.sendLine(line)

class TcpServerFactory(Factory):
    def __init__(self):
        self.clients = set() # set集合用于保存所有连接到服务器的客户端

    def buildProtocol(self, addr):
        return TcpServerHandle(self)

reactor.listenTCP(8080, TcpServerFactory())
reactor.run()