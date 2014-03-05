hotcode
=======

![image](https://api.travis-ci.org/khotyn/hotcode.png)

HotCode is a JVM agent to enable runtime class reloading. It is currently at a very early stage of developing.

### To Contributors

Make sure your code can pass all the test every time you push code. Run the following command to run all test cases:

```
mvn install && bash test.sh
```

Run the following command to run a single test case:

```
bash test.sh addPrimaryFieldTest
```

`addPrimaryFieldTest` can be any other test case's name.

如果使用的时mac系统，可以试试
bash test4mac.sh

支持1.6版本的java