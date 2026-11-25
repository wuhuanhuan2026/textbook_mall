> 💡 **硬核开发日志**：[点击查看 20+ 项微服务与前后端联调踩坑排错记录](开发问题与解决方案.md)（包括从底层环境、到代码调试、再到运维操作的全链路记录：1. 本地多网卡 Nacos 注册 IP 冲突与 Feign 跨服务调用 404；2. Vue3/Element Plus 分页与状态管理高频 Bug；3. MySQL 表结构冲突与数据清洗；4. 开发环境搭建与核心中间件（Nacos/MySQL/Redis）启停指南；5. CMD/PowerShell 批量文件导出与工程目录树查看；6. AI 辅助生成代码的审查与排错纠错）


# 教材商城系统（book-mall）

## 1. 项目简介
基于 Spring Cloud Alibaba + Vue 3 的微服务教材商城。包含用户、分类、教材、购物车、订单、支付、管理员、地址、评价 9 个微服务 + API 网关。

## 2. 功能列表
- 用户：注册、登录、信息管理
- 教材：浏览、搜索、分类筛选、详情
- 购物车：加入、修改数量、删除、清空
- 订单：创建、支付、取消、收货
- 管理后台：仪表盘、教材管理、订单管理、用户管理

## 3. 技术栈
- 前端：Vue 3 + Vite + Vue Router + Pinia + Element Plus
- 后端：JDK 8 + Spring Boot 2.7.18 + Spring Cloud 2021.0.5 + Spring Cloud Alibaba 2021.0.5.0 + MyBatis-Plus
- 中间件：Nacos 2.2.3 + Redis + MySQL 5.7+
- 网关：Spring Cloud Gateway

## 4. 目录结构
book-mall/
├── backend/       后端微服务
├── frontend/      前端工程
├── database/      SQL 脚本
├── project-docs/  项目文档
└── evidence/      实训截图

## 5. 环境要求
- JDK 8 (Temurin 1.8.0_492 验证通过)
- Maven 3.9+
- Node.js 22+
- MySQL 5.7+ (5.7.44 / 8.x 均可)
- Redis
- Nacos 2.2.3

## 6. 数据库初始化
依次执行（脚本内部已含 USE book_mall;）：
mysql -u root -p < database/01-create-db.sql
mysql -u root -p < database/02-schema.sql
mysql -u root -p < database/03-seed.sql

验证：
mysql -u root -p -e "USE book_mall; SELECT COUNT(*) FROM tb_book;"
应返回 65

## 7. 后端启动
1. 先启动 Nacos 和 Redis
2. 在 IDEA 里依次启动各微服务：User → Category → Book → Cart → Order → Payment → Admin → Address → Review → Gateway
3. 启动前设置环境变量：
set DB_PASSWORD=你的MySQL密码
set DRUID_LOGIN_PASSWORD=你的Druid密码
set JWT_SECRET=一个长随机字符串
4. 访问 http://localhost:8090/api/books 应返回 200

## 8. 前端启动
cd frontend
npm install
npm run dev
打开终端显示的地址（默认 http://localhost:8979/）

## 9. 测试账号
| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 用户 | testuser | 123456 |

仅演示使用，请勿用于生产环境。

## 10. 常见问题
- 端口占用：netstat -ano | findstr :端口
- 数据库连接失败：检查 DB_PASSWORD 环境变量是否设置
- 跨域：前端通过 Vite 代理到网关，无需额外配置
- npm ci 报 EPERM：先关掉 npm run dev 和编辑器，或改用 npm install

## 11. 构建与部署
cd frontend && npm run build
cd backend && mvn clean package

## 12. 版本历史
- v1.0.0-rc1 (2026-10-07)：候选交付版本
