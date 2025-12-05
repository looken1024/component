## Agent

### 什么是Agent

就是一种能够根据给定的信息自己进行推理、规划和采取行动的AI智能系统。

核心：自行规划决策 -> 自行调用外部工具 -> 自执行适应

简述：能够思考、记忆和完成任务的数字员工


自动化工作流不是Agent！！！遵循着静态不变的规则

自动化工作流：预先定义的固定步骤

Agent：动态的、灵活的、有自主推理能力的


### Agent怎么工作

三个主要组件：大脑+记忆+工具

大脑：为Agent提供智能d的大语言模型，比如ChatGPT、Claude、Gemini，负责推理、规划和语言生成

记忆：记住之前的聊天；以及执行任务过程中的操作；利用这些背景和上下文，统一目标，做出更好的决策

工具：
    1、主动获取需要的上下文信息，检索数据，搜索网页，提取文档信息
    2、采取行动，收发邮件，增删数据库，创建行程，运行代码
    3、编排，调用其他代理，触发工作流，连接操作

多Agent：
    管理中枢
    调查
    写作
    写代码
    做成网页

原则：使用最简单可行的方式达到目的
    1、如果单个Agent能解决，就用单Agent
    2、如果自动化工作流就能解决，就用工作流
    3、如果提示词能解决，就用提示词

护栏、安全：解决产生幻觉、陷入循环、错误行为，应对风险

### Agent能做什么

API：
    GET：获取信息，例：天气信息、热搜信息
    POST：发送消息，接受反馈

    提示词 -> POST -> API -> 回答

n8n：无需代码编写、可视化、节点式，搭建工作流，Agent的平台，德国 19年开始，开源，入门快

本地安装n8n：
    1、Docket：桌面端 Resources->Disk image location，选定位置
    2、shell：docker volume create n8n_data
              docker run -d --name n8n --restart unless-stopped -p 5678:5678 -v n8n_data:/home/node/.n8n docker.n8n.io/n8nio/n8n

        -d                            # 后台运行（detached mode）
        --name n8n                    # 容器命名为 "n8n"
        --restart unless-stopped      # 自动重启策略（除非手动停止）
        -p 5678:5678                  # 端口映射（主机端口:容器端口）
        -v n8n_data:/home/node/.n8n   # 挂载数据卷到容器内的n8n数据目录
        docker.n8n.io/n8nio/n8n       # 使用的n8n官方镜像
    3、关闭：docker stop n8n
    4、启动：docket start n8n


### Agent实战

收集：今日新闻、你的日程

发送：新闻早报、日程建议

步骤：
    1、创建一个新的工作流
    2、点击+，选一个“按计划”的触发器，设置运行时间
    3、点击+，选择AI->AI Agent
        输入，配置，输出
       点击返回画布
    4、点击连接线->垃圾箱图标，断开连接
    5、配置大脑
        DeepSeek：创建API密钥，连接
    6、配置记忆
        点击+，选择“简单内存”
        上下文窗口长度：5
    7、聊天
        点击+，选择“聊天消息”
    8、工具
        非内置：http请求，社区开发者（搜包，安装）
        飞书：
            多维表格
                工作流1：收集当天新闻
                工作流2：处理数据，按创建时间筛选
            日历：
                获取日程列表 日历ID（calender） 开通权限
                开始时间，结束时间
    9、发送消息机器人
        输出位置：http请求 飞书群聊 webhook
        


## Dify制作

1、创建空白应用 选择Agent
2、点击工具，选择金融->股票查询

编排：
    提示词：如果用户是问股票相关的事，就调用股票查询工具查查相关信息，再给出用户投资简易，如果不是股票类的问题，就用贴吧老哥的预期嘲讽他。


## 借Siri调用DeepSeek

1、新建快捷指令：听写文本，语言设置为中文
2、获取URL内容：URL替换为实际的url，POST
    头部：Content-Type：Application/json、Authoritarian
    请求体：JSON->文本 model、数组 message role content选择变量->听写的文本
3、获取词典值：choices.1.message.content.content
4、朗读文本
5、显示结果
6、添加快捷方式到桌面


