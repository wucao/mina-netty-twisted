# -*- coding:utf-8 –*-

from struct import pack, unpack
from twisted.internet.protocol import Factory
from twisted.internet.protocol import Protocol
from twisted.internet import reactor

# 编码、解码器
class MyProtocol(Protocol):

    # 用于暂时存放接收到的数据
    _buffer = b""

    def dataReceived(self, data):
        # 上次未处理的数据加上本次接收到的数据
        self._buffer = self._buffer + data
        # 一直循环直到新的消息没有接收完整
        while True:
            # 如果header接收完整
            if len(self._buffer) >= 4:
                # 按小字节序转int
                length, = unpack("<I", self._buffer[0:4])
                # 如果body接收完整
                if len(self._buffer) >= 4 + length:
                    # body部分
                    packet = self._buffer[4:4 + length]
                    # 新的一条消息接收并解码完成，调用stringReceived
                    self.stringReceived(packet)
                    # 去掉_buffer中已经处理的消息部分
                    self._buffer = self._buffer[4 + length:]
                else:
                    break;
            else:
                break;

    def stringReceived(self, data):
        raise NotImplementedError

    def sendString(self, string):
        self.transport.write(pack("<I", len(string)) + string)

# 逻辑代码
class TcpServerHandle(MyProtocol):

    # 实现MyProtocol提供的stringReceived而不是dataReceived，不然无法解码
    def stringReceived(self, data):

        # data为MyProtocol解码后的数据
        print 'stringReceived:' + data

        # 调用sendString而不是self.transport.write，不然不能进行编码
        self.sendString("收到")

factory = Factory()
factory.protocol = TcpServerHandle
reactor.listenTCP(8080, factory)
reactor.run()