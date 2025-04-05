📚简介

在日常通过TCP、UDP接入自定义的协议时候，如果每个协议都开一个端口，会对端口资源造成巨大的浪费。为提高端口复用，实现了单端口实现多协议兼容的小工程，
且TCP和UDP传输方式也能自由切换。

本工程使用reactor-netty实现了一套简单的接入、解析、处理的流程，通过配置文件实现协议的添加、修改，提高端口的复用。

🍺使用方式

1、实现协议的解码和编码方式，实现PipelineInitializer类，组装pipeline。

2、实现ProtocolHandler类，具体处理的业务逻辑。

3、在ClientProtocol枚举类注册协议的处理类。

4、在application.yml中配置开启的协议。

5、可自行接入nacos。

🚨 默认端口

tcp:7788

udp:7788

📱 沟通交流

QQ：949561664

微信：DGUT20062014DJH