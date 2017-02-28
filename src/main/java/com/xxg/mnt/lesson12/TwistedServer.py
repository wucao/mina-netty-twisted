# -*- coding:utf-8 –*-

from twisted.internet import reactor, ssl
from twisted.web import server, resource

sslContext = ssl.DefaultOpenSSLContextFactory(
    '/Users/wucao/Desktop/https/2_gw2.vsgames.cn.key',  # 私钥
    '/Users/wucao/Desktop/https/1_gw2.vsgames.cn_bundle.crt',  # 证书
)

class MainResource(resource.Resource):

    isLeaf = True

    # 用于处理GET类型请求
    def render_GET(self, request):

        # name参数
        name = 'World'
        if request.args.has_key('name'):
            name = request.args['name'][0]

        # 设置响应编码
        request.responseHeaders.addRawHeader("Content-Type", "text/html; charset=utf-8")

        # 响应的内容直接返回
        return "<html><body>Hello, " + name + "</body></html>"


site = server.Site(MainResource())
reactor.listenSSL(8080, site, sslContext)
reactor.run()