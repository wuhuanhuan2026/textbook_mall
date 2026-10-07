**CSS 变量规范：**

```css
:root {
  /* ===== 颜色 ===== */
  --color-primary: #176b55;
  --color-primary-hover: #145a47;
  --color-primary-light: #e8f5f1;
  --color-success: #20865a;
  --color-warning: #b7791f;
  --color-error: #c53030;
  --color-info: #1976d2;

  --color-text: #202523;
  --color-text-secondary: #68706d;
  --color-text-placeholder: #a0a8a5;
  --color-border: #d7ddda;
  --color-bg: #f6f8f7;
  --color-bg-card: #ffffff;

  /* ===== 间距 ===== */
  --space-1: 4px;
  --space-2: 8px;
  --space-3: 12px;
  --space-4: 16px;
  --space-5: 24px;
  --space-6: 32px;
  --space-page: 24px;

  /* ===== 字号 ===== */
  --font-xs: 12px;
  --font-sm: 14px;
  --font-base: 16px;
  --font-lg: 18px;
  --font-xl: 22px;
  --font-2xl: 28px;

  /* ===== 行高 ===== */
  --line-tight: 1.3;
  --line-base: 1.5;
  --line-loose: 1.8;

  /* ===== 其他 ===== */
  --content-width: 1180px;
  --radius: 6px;
  --radius-card: 8px;
  --radius-control: 4px;
}
```

**颜色用途：**

| 颜色 | 用途 | 示例 |
|------|------|------|
| primary | 主按钮、链接、选中态 | "加入购物车" |
| success | 成功提示、已完成状态 | "订单已完成" |
| warning | 警告提示、待处理状态 | "库存仅剩5件" |
| error | 错误提示、危险操作 | "删除"、"库存不足" |
| info | 信息提示、待发货状态 | "待发货" |

**可访问性检查：**

| 检查项 | 要求 | 通过 |
|--------|------|------|
| 正文对比度 | ≥ 4.5:1 | ✅ |
| 大字对比度 | ≥ 3:1 | ✅ |
| 主色对比度 | ≥ 4.5:1 | ✅ |
| 状态区分 | 颜色 + 文字 | ✅ |
| 焦点可见 | outline | ✅ |

**预期结果与人工检查：**
- 颜色含义稳定且对比度可读。
- 同类页面使用相同间距和控件尺寸。
- 规范可以转换为CSS变量。

**实际结果：**
- ✅ CSS 变量已定义。
- ✅ 颜色用途已明确。
- ✅ 可访问性检查全部通过。

**常见错误与定位方法：**
- 只写"简洁美观"无法执行，应给出具体值。
- 错误状态仅用颜色区分会影响可访问性，应配合文字。

---
