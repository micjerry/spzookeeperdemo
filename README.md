验证Spring ZooKeeper方案

  
验证ZooKeeper日常操作：
   
    (1) 执行zookeeper-common-oper 端口8080
   
    (2) 验证增删改查和watcher、listener
        curl http://localhost:8080/zookeeper/test
        查看日志打印
   
验证ZooKeeper Leader Selector：
    (1) 执行zookeeper-leader-selector(配置application.yml: 端口8080,实例名称(instance)numberone)
    (2) 执行zookeeper-leader-selector(配置application.yml: 端口8081,实例名称(instance)numbertwo)
    (3) 查看日志打印，可以看到两个实例获得leader释放leader身份的过程
   
 
