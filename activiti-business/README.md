更新日志 20170830
1.增加Restful接口代理类
2.抽象类ProxyRestFul、通用实现类ProxyRestFulCommon
3.目前支持的代理有POST、GET、DELETE、PUT，后续如果有需要可以新增
4.如果因为业务需要使用调用结果，清新建类似ProxyRestFulCommon，在方法processResult中实现结果解析和参数赋值到流程变量中
5.目前需要输入的参数有4个
apiIp       String/StringExpression   ip地址及端口等存储在配置文件restful-ipconfig中,只需要键值 
methodType  String/StringExpression   目前只能为POST、GET、DELETE、PUT
apiUrl      String/StringExpression   eg>. /restfulTestAPI/postJson/${a}
content     String/StringExpression   eg>. ${p1}

更新日志 20170901
1.将原有的代理类由business工程转移为了manager工程
2.调用类为com.zlst.proxy.ProxyRestFulCommon，使用方式和以前相同
3.需要用MAVEN更新放在私服上的manager的jar包