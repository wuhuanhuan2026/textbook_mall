# 教材商城系统 - 项目档案

## 项目信息
- 中文名：教材商城系统
- 英文名：Book Mall System
- 项目根目录：D:\study\Textbook_Mall\book-mall
- 创建日期：2026-09-01

## 成员信息
| 姓名 | 学号 | 角色 |
|------|------|------|
| 吴欢欢 | 202300122 | 全栈开发 |

## 端口约定
| 服务 | 端口 |
|------|------|
| 前端（Vite） | 8979 |
| 网关（Gateway） | 8090 |
| 用户服务（User） | 8081 |
| 分类服务（Category） | 8082 |
| 教材服务（Book） | 8083 |
| 购物车服务（Cart） | 8084 |
| 订单服务（Order） | 8085 |
| 支付服务（Payment） | 8086 |
| 管理后台（Admin） | 8087 |
| 地址服务（Address） | 8088 |
| 评价服务（Review） | 8089 |
| MySQL | 3306 |
| Nacos | 8848 |
| Redis | 6379 |

## 技术栈（JDK 8版本）
- 前端：Vue 3 + Vite + Vue Router + Pinia + Axios + Element Plus + ECharts
- 后端：JDK 8 + Spring Boot 2.7.18 + MyBatis-Plus 3.5.3.1 + MySQL 8
- 微服务：Spring Cloud 2021.0.5 + Spring Cloud Alibaba 2021.0.5.0 + Nacos 2.2.3
- 工具：Git、VS Code、IntelliJ IDEA、Navicat
- 数据库：book_mall

## 目录结构
- backend/       后端微服务（Common、User、Book、Category、Cart、Order、Payment、Admin、Address、Review、Gateway）
- frontend/      前端工程
- database/      数据库脚本
- project-docs/  项目文档
- evidence/      证据材料



---

## 项目定义卡

### 一句话定义
为高校师生提供一个教材信息发布与在线购买平台。

### 目标用户
- 学生：浏览、购买教材
- 管理员：发布教材、管理订单

### 三条目标
1. 管理员能发布教材信息
2. 学生能浏览、搜索、购买教材
3. 管理员能查看订单和用户信息

### 范围内
- 教材管理
- 分类管理
- 购物车
- 订单管理
- 用户管理

### 范围外
- 支付接口对接
- 短信通知
- 真实校园统一认证

### 约束
- JDK 8 + Spring Boot 2.7.18
- 前端 Vue 3 + Vite
- 数据库 MySQL 5.7
- 微服务架构：Nacos + Gateway

### 成功标准
能够完成教材发布 → 浏览 → 加入购物车 → 下单 → 查看订单闭环。