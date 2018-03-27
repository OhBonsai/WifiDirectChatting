# WifiDirectChatting
基于Wifi-Direct的Android通讯软件

## 介绍
软件根据google提供的demo改的，原本是只能传送图片的。
Android手机平台通讯软件，功能有传文件、对讲、定位、文字聊天。
通信方式基于wifi direct。相当于高级蓝牙。手机要求版本4.0以上。
且支持wifi direct功能（大多都有）

## 功能和Bug
- 文件传输： 传文件调用的是ES文件管理器得到的文件路径，所以你要有ES文件管理器。传文件用的是intentservice+AsyncTask,只支持client端传给server端。原理是一样的。没写。
- 对讲功能： 退出对讲activity的时候有bug,大概是什么没有关闭吧。懒得改了。
- 定位功能： 定位注释了google定位的代码，用的是百度定位的代码，所以你要自己申请一个百度定位的KEY。修改manifest
- 文字聊天： 代码结构和对讲差不多。在drewable里面有很多没用的图片和xml文件，那是因为我界面复制微信的。所有资源直接拷过来的。关系不大。


