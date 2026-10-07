**设计令牌草案：**

```css
:root {
  /* 颜色 */
  --color-primary: #176B5B;
  --color-success: #2E7D32;
  --color-warning: #B26A00;
  --color-danger: #C62828;
  --color-info: #1976D2;
  --color-text: #202523;
  --color-text-muted: #68706D;
  --color-border: #D7DDDA;
  --color-bg: #F6F8F7;
  --color-bg-card: #FFFFFF;

  /* 间距 */
  --space-1: 4px;
  --space-2: 8px;
  --space-3: 12px;
  --space-4: 16px;
  --space-5: 24px;
  --space-6: 32px;

  /* 字号 */
  --font-xs: 12px;
  --font-sm: 14px;
  --font-base: 16px;
  --font-lg: 18px;
  --font-xl: 22px;
  --font-2xl: 28px;

  /* 其他 */
  --content-width: 1180px;
  --radius: 6px;
  --radius-card: 8px;
}
```

**可访问性检查项：**

| 检查项 | 要求 |
|--------|------|
| 正文对比度 | ≥ 4.5:1 |
| 大字对比度 | ≥ 3:1 |
| 主色与背景 | 对比度可读 |
| 状态区分 | 颜色 + 文字/图标 |
| 焦点可见 | 按钮/链接有焦点样式 |

**预期结果与人工检查：**
- 主色与状态色用途明确，正文对比度足够。
- 站点地图包含登录、教材列表、详情、购物车和管理端页面。
- 管理端与学生端导航边界清楚。

**实际结果：**
- ✅ 设计令牌已定义。
- ✅ 可访问性检查项已明确。

**常见错误与定位方法：**
- 页面很多但层级不清时按角色拆分。
- 只给"好看"要求会导致AI随机设计，应明确任务密度和阅读对象。

---