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


### Agent实战

收集：今日新闻、你的日程

发送：新闻早报、日程建议
