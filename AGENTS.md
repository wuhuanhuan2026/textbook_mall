# AI 协作规则

## 项目基本信息
- 项目名：教材商城系统（book-mall）
- 前端：Vue 3 + Vite + Vue Router + Pinia + Axios + Element Plus + ECharts
- 后端：JDK 8 + Spring Boot 2.7.18 + MyBatis-Plus 3.5.3.1 + MySQL 8
- 微服务：Spring Cloud 2021.0.5 + Spring Cloud Alibaba 2021.0.5.0 + Nacos 2.2.3
- 端口约定：前端 8979，网关 8090，MySQL 3306，Nacos 8848
- 数据库：book_mall

## 目录约定
- frontend/       前端工程
- backend/        后端多模块工程（11 个微服务）
- database/       数据库脚本
- project-docs/   项目文档
- evidence/       环境与验证证据

## 命名约定
- 数据库表统一使用 tb_ 前缀
- 后端模块使用大驼峰，如 Book、Category、Gateway
- 前端组件使用大驼峰，页面文件使用大驼峰

## AI 可以做什么
- 补充方案、生成局部代码、辅助排错
- 提供命令和验证方法
- 生成文档草稿

## AI 不可以做什么
- 不得修改密钥、密码、token
- 不得一次性重写整个项目
- 不得引入未经确认的依赖
- 不得修改与当前任务无关的文件

## 每次输出要求
1. 一次只改当前任务相关文件
2. 明确给出文件路径
3. 明确给出验证方法（命令或页面操作）
4. 说明人工需要核验的点
5. 涉及密钥、个人隐私、项目数据时必须提醒

## 人工核验要求
- 所有 AI 输出必须经过人工检查
- 运行结果、日志、截图作为最终依据