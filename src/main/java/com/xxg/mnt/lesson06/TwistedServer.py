# -*- coding:utf-8 –*-

from twisted.protocols.basic import LineOnlyReceiver
from twisted.internet.protocol import Factory
from twisted.internet import reactor

class TcpServerHandle(LineOnlyReceiver):

    # 连接相关的信息直接保存为Protocol继承类TcpServerHandle的属性
    counter = 0;

    def lineReceived(self, data):
        self.counter += 1
        print "第" + str(self.counter) + "次请求:" + data

factory = Factory()
factory.protocol = TcpServerHandle
reactor.listenTCP(8080, factory)
reactor.run()