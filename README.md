# imuwatchface

## 所谓版本不匹配问题

app目录下的`build.gradle`中`dependencies`包含的如下几个：

```
com.google.android.gms:play-services-wearable:17.0.0
com.google.android.gms:play-services-fitness:18.0.0
com.google.android.gms:play-services-auth:18.0.0
com.google.android.gms:play-services-location:17.0.0
```

为了使用类`ActivityRecognitionClient`，至少要保证`com.google.android.gms:play-services-location`的版本号在`12.0.0`，[点此了解详情](https://developer.android.google.cn/guide/topics/location/transitions?hl=zh-cn#java)，而手表的`google play service`版本为`11.9.66(Huawei Watch GT 2)、11.0.55(Huawei Watch 2)`，该类在文件`ActRecognitionService.java`中被使用到

## 项目介绍

基于我之前阅读代码的理解，主要是介绍`src`目录下的代码文件

- `util`文件夹：一些工具类
    - `re.java`：使用正则表达式进行字符串匹配的工具类
    - `ServerAddress.java`：获取本地IP、网关IP，以及服务器是否在正常工作
    - `UploadUtil.java`：基于`OkHttp`使用`post`方式上传文件的一个工具类
- `ActRecognitionService.java`：进行动作识别的一个后台服务
- `AwsS3.java`：之前没弄明白，简单查了下，是操作由`Amazon`推出的一个云存储接口的类
- `ChargingState`：是一个基于**广播**机制，在用户进行充电的状态下打开WiFi并进行数据上传的一个类
- `DetectedActivitiesIntentService.java`：处理动作识别结果的一个后台服务
- `IMUSensorListener.java`：侦听传感器
- `IOLogData.java`：传感器数据记录，写文件接口
- `LogService.java`：传感器数据记录
- `MyWatchFace.java`：表盘，在这里绘制表盘，设计表盘上的部件
- `OneValueSensorListener.java`：侦听传感器
- `PermissionActivity.java`：单纯的权限请求，可以单独运行，有点像是独立于表盘的一个应用，虽然他属于表盘这整个应用

