#IdTwitterGet

##已完成功能
1. 按Twitter的snowflake代码实现[[Twitter原文件](https://github.com/twitter/snowflake/blob/scala_28/src/test/scala/com/twitter/service/snowflake/IdWorkerSpec.scala)].
2. 代码内有完善的中文注释，便于在项目中修改使用.
3. 在本地机器测试，每个线程每毫秒可以生成20个ID(CPU:i5-3210M，内存6G，jdk1.7，Win8).

##后期规划
3. 后期会实现spring,jfinal,nutz等版本，包括xml和Annotation方式，便于在项目中直接调用生成ID.