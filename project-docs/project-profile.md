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