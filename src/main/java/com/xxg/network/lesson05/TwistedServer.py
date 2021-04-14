# -*- coding:utf-8 –*-

from struct import pack, unpack
from twisted.internet.protocol import Factory
from twisted.internet.protocol import Protocol
from twisted.internet import reactor
import StudentMsg_pb2

# protobuf编码、解码器
class ProtobufProtocol(Protocol):

    # 用于暂时存放接收到的数据
    _buffer = b""

    def dataReceived(self, data):
        # 上次未处理的数据加上本次接收到的数据
        self._buffer = self._buffer + data
        # 一直循环直到新的消息没有接收完整
        while True:
            # 如果header接收完整
            if len(self._buffer) >= 4:
                # header部分，按大字节序转int，获取body长度
                length, = unpack(">I", self._buffer[0:4])
                # 如果body接收完整
                if len(self._buffer) >= 4 + length:
                    # body部分，protobuf字节码
                    packet = self._buffer[4:4 + length]

                    # protobuf字节码转成Student对象
                    student = StudentMsg_pb2.Student()
                    student.ParseFromString(packet)

                    # 调用protobufReceived传入Student对象
                    self.protobufReceived(student)

                    # 去掉_buffer中已经处理的消息部分
                    self._buffer = self._buffer[4 + length:]
                else:
                    break;
            else:
                break;

    def protobufReceived(self, student):
        raise NotImplementedError

    def sendProtobuf(self, student):
        # Student对象转为protobuf字节码
        data = student.SerializeToString()
        # 添加Header前缀指定protobuf字节码长度
        self.transport.write(pack(">I", len(data)) + data)

# 逻辑代码
class TcpServerHandle(ProtobufProtocol):

    # 实现ProtobufProtocol提供的protobufReceived
    def protobufReceived(self, student):

        # 将接收到的Student输出
        print 'ID:' + str(student.id)
        print 'Name:' + student.name
        print 'Email:' + student.email
        print 'Friends:'
        for friend in student.friends:
            print friend

        # 创建一个Student并发送给客户端
        student2 = StudentMsg_pb2.Student()
        student2.id = 9
        student2.name = '服务器'.decode('UTF-8') # 中文需要转成UTF-8字符串
        student2.email = '123@abc.com'
        student2.friends.append('X')
        student2.friends.append('Y')
        self.sendProtobuf(student2)

factory = Factory()
factory.protocol = TcpServerHandle
reactor.listenTCP(8080, factory)
reactor.run()