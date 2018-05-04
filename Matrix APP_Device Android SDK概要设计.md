# Matrix APP/Device Android SDK概要设计

标签（空格分隔）： 未分类

---

[TOC]

# 概述

Matrix | Git仓库 | 分支 | 功能
- | - | -
APP SDK | sdk/app-android-sdk | master/develop | 用户访问AC平台微服务/订阅AC平台消息/局域网交互
Device SDK | sdk/ac-device-service | master/develop | 设备连接AC平台/访问AC平台微服务

# Matrix APP Android SDK

## 一、访问AC平台微服务

访问**预建**微服务

```seq
APP->zc.account: login(HTTP)
zc.account-->APP: user.accessToken(HTTP)
APP->zc.bind: listDevices(HTTP):user.accessToken
zc.bind-->APP: devices(HTTP)
```

访问UDS微服务

```seq
APP->uds.name: uds.api(HTTP):user.accessToken
uds.name-->APP: devices(HTTP)
```

## 二、订阅AC平台推送

用户登录后，SDK与推送服务器建立WebSocket长连接

```seq
APP->WS网关: connect(WebSocket)
WS网关-->APP: 建立WebSocket连接
APP->WS网关: login(WebSocket):user.accessToken
WS网关-->APP: ack(WebSocket)
```

当有数据时

```seq
APP->WS网关: subsribe(WebSocket):topic
WS网关-->APP: ack(WebSocket)
Note right of WS网关: 推送被触发
WS网关->APP: data(WebSocket)
```

## 三、局域网交互

### 配置设备无线连接

#### SmartConfig

大部分无线网络都是加密的，即数据链路层的Wi-Fi帧（Frame）是加密的。
SmartConfig协议即把Wi-Fi协议数据包长度作为信息载体，把目标无线路由的名称（SSID）和密码（PWD）编码作为信息，编码成不同长度的Wi-Fi帧发射出去，进而帮助设备连接目标无线网络的一种方式

```seq
APP->无线路由: 广播SSID/PWD(Wi-Fi)
无线路由-->无线路由: 转发SSID/PWD(Wi-Fi)
Note right of Device: 接收到SSID/PWD(Wi-Fi)
Device->无线路由: 连接无线路由
```

一般需要发射端（如手机）和接受端（如智能设备）各部署一套编解码库。每个芯片厂商都实现了定制版本的SmartConfig协议，所以给设备SmartConfig配网，需要调用对应的芯片厂商APP端的SmartConfig协议库

#### AP Hotspot

```seq
Device-->Device: 转为AP模式
APP->Device: 连接Device无线路由
APP->Device: 发送目标SSID/PWD(UDP)
Device-->APP: ack(UDP)
Device-->Device: 转为正常模式
APP->目标无线路由: 连接
Device->目标无线路由: 连接
```

### 局域网扫描（发现）

```seq
APP->无线路由: 广播查询包(UDP)
无线路由->Device: 转发查询包(UDP)
Device-->APP: 设备信息(UDP)
```

### 局域网控制

1. 无加密
2. 静态加密（依规则计算，出厂静态写入设备；APP同规则计算获取）
3. 动态加密（设备联网时，AC平台下发；APP用户绑定后获取）

```seq
APP->Device: command(UDP):SessionKey
Device-->APP: result(UDP)
```

### 局域网订阅

```seq
APP->无线路由: 定时广播subscribe(UDP)
无线路由->Device: 转发subscribe(UDP)
Note right of Device: 属性变化
Device->APP: data(UDP)
```

# Matrix Device Android SDK

设备联网后，SDK向Device负载服务请求Device网关地址，然后与设备网关服务器建立TCP长连接

**如果需要主动访问AC平台微服务，还需要初始化HTTP AccessToken**

## 一、初始化

### 连接Device负载服务

```seq
Device->Device负载服务: 建立TCP连接
Device负载服务-->Device: 建立
Device->Device负载服务: authenticate(TCP)
Device负载服务-->Device: Device网关地址(TCP)
Note left of Device: 断开TCP连接
```

### 连接Device网关

```seq
Device->Device网关: 建立TCP连接
Device网关-->Device: 建立
Device->Device网关: 请求接入(TCP)
Device网关-->Device: SessionKey(TCP)
Device->Device网关: 上报设备配置(TCP):SessionKey
Device网关-->Device: ack(TCP)
```

### 获取HTTP AccessToken

```seq
Device->zc.warehouse: activateDevice(HTTP)
zc.warehouse-->Device: device.accessToken(HTTP)
```

## 二、云端交互

### 云端控制

APP控制设备

```seq
APP->Device网关: command(HTTP)
Device网关->Device: command(TCP)
Device-->Device网关: result(TCP)
Device网关-->APP: result(HTTP)
```

云端服务（如UDS）控制设备

```seq
SERVICE->Device网关: command(HTTP)
Device网关->Device: command(TCP)
Device-->Device网关: result(TCP)
Device网关-->SERVICE: result(HTTP)
```

## 三、OTA检测及下载

### OTA检测

1. 服务器通过TCP推送更新链接（要求强制更新）
2. 设备主动轮询AC平台zc-ota服务接口，检查更新

#### 服务器推送

```seq
Device->Device网关: TCP连接已经建立
Note right of Device网关: 收到更新推送
Device网关->Device: ota-file-url(TCP)
```

#### 主动轮询

```seq
Device->zc.ota: checkUpdate(HTTP)
zc.ota-->Device: ota-file-url(HTTP)
```

