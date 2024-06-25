
# The Code

### 基于Springboot+Vue3的STEM知识交流论坛

#### 角色包含：普通用户、管理员

#### 功能包含：

- 注册、登录、忘记密码

- 用户信息设置、隐私设置

- 发帖、帖子列表展示、帖子分区展示、帖子搜索、查看最热帖子、删除帖子、管理员删帖

- 发表评论、删除评论、管理员删评、点赞、收藏、我的收藏夹、消息模块

- AI实时对话、查看历史对话


## 在本地运行
- 启动Redis

- 在端口9999上启动minio（可在配置文件中修改端口）

- 前往前端项目目录

```bash
  cd my-project-frontend
```

- 安装依赖

```bash
  npm install
```

- 启动服务器

```bash
  npm run dev
```

- 前往后端目录

```bash
  cd my-project-backend
```

- 运行MyProjectBackendApplication类中的main方法
## 版本要求
**Springboot:** 3以上

**MySQL:** 5.7

**需要安装Redis、RabbitMQ和minio**
## 自定义配置项

**application-dev.yml为开发配置：**
```
# 配置SpringDoc工具，用于生成API文档
springdoc:
  # 指定生成文档的URL路径模式，这里匹配以/api开头的路径
  paths-to-match: /api/**
  # Swagger UI配置，按照字母顺序排序API操作
  swagger-ui:
    operations-sorter: alpha

# Spring框架配置
spring:
  # 邮件服务器配置
  mail:
    # 邮件服务器的主机地址，这里配置为QQ邮箱的SMTP服务器地址
    host: smtp.qq.com
    # 发送邮件的邮箱账户
    username: **********@qq.com
    # 邮箱的SMTP授权码，用于发送邮件的验证
    password: **********
    properties:
      # 发送邮件时使用的发件人地址
      from: **********@qq.com
  # RabbitMQ消息队列配置
  rabbitmq:
    # RabbitMQ服务器的地址
    addresses: localhost
    # RabbitMQ的用户名
    username: *****
    # RabbitMQ的密码
    password: *****
    # RabbitMQ的虚拟主机
    virtual-host: /
  # 数据源配置
  datasource:
    # 数据库连接URL
    url: jdbc:mysql://localhost:3306/study
    # 数据库的用户名
    username: ****
    # 数据库的密码
    password: ****
    # 数据库驱动的全类名
    driver-class-name: com.mysql.cj.jdbc.Driver
  # 安全配置
  security:
    # JWT令牌配置
    jwt:
      # JWT签名密钥
      key: 'abcdefghijklmn'
      # 令牌的有效期，单位小时
      expire: 72
      # 令牌使用限制配置
      limit:
        # 基础令牌使用次数
        base: 10
        # 升级令牌使用次数
        upgrade: 300
        # 令牌使用频率限制，每分钟次数
        frequency: 30
    # 过滤器配置
    filter:
      # 过滤器的顺序
      order: -100
  # Web应用配置
  web:
    # 邮件验证配置
    verify:
      # 邮件验证的有效期，单位分钟
      mail-limit: 60
    # 流量控制配置
    flow:
      # 流量控制周期，单位秒
      period: 3
      # 周期内允许的最大请求次数
      limit: 50
      # 超出限制后的阻断时间，单位秒
      block: 30
    # 跨域资源共享配置
    cors:
      # 是否允许发送Cookie
      credentials: false
      # 允许的HTTP方法
      methods: '*'
      # 允许的来源
      origin: "*"
  # Minio对象存储配置
  minio:
    # Minio服务器的端点地址
    endpoint: 'http://localhost:9999'
    # Minio的用户名
    username: '*****'
    # Minio的密码
    password: '*****'
  # 天气服务API密钥
  weather:
    key: 47d29ad789c640c08a537200d9070ca7
  # 阿里云通义千问AI服务配置
  ai:
    # 阿里云AI服务的密钥
    key: ********
```
**application-prod.yml为生产环境配置，与上面类似**