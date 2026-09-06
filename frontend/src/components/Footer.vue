<template>
  <footer class="footer">
    <div class="footer-container">
      <div class="footer-content">
        <!-- 品牌信息 -->
        <div class="footer-section brand">
          <h4>📚 教材商城</h4>
          <p>精选教材，助力学习</p>
          <p class="slogan">让每一本教材都物有所值</p>
        </div>
        
        <!-- 关于我们 -->
        <div class="footer-section">
          <h5>关于我们</h5>
          <a href="#">公司简介</a>
          <a href="#">联系我们</a>
          <a href="#">加入我们</a>
        </div>
        
        <!-- 帮助中心 -->
        <div class="footer-section">
          <h5>帮助中心</h5>
          <a href="#">购物指南</a>
          <a href="#">支付方式</a>
          <a href="#">退款政策</a>
        </div>
        
        <!-- 联系方式 -->
        <div class="footer-section">
          <h5>联系方式</h5>
          <p>📧 support@bookmall.com</p>
          <p>📞 400-888-8888</p>
          <p>🕐 工作日 9:00-18:00</p>
        </div>
      </div>
      
      <div class="footer-bottom">
        <p>© 2026 教材商城. All rights reserved.</p>
        <p>粤ICP备 2026000000 号</p>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  background: #2c3e50;
  color: #ffffff;
  margin-top: 40px;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px 20px;
}

.footer-content {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1.5fr;
  gap: 40px;
  margin-bottom: 30px;
}

.footer-section h4 {
  font-size: 20px;
  margin-bottom: 8px;
  color: #ffffff;
}

.footer-section h5 {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #c0c4cc;
}

.footer-section p {
  font-size: 13px;
  color: #a8b0b8;
  margin: 4px 0;
  line-height: 1.6;
}

.footer-section .slogan {
  font-size: 12px;
  color: #7a8a9a;
  margin-top: 4px;
}

.footer-section a {
  display: block;
  font-size: 13px;
  color: #a8b0b8;
  text-decoration: none;
  margin: 6px 0;
  transition: color 0.3s;
}

.footer-section a:hover {
  color: #ffffff;
}

.footer-bottom {
  border-top: 1px solid #3d5060;
  padding-top: 16px;
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #7a8a9a;
}

@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: 1fr 1fr;
    gap: 24px;
  }
  
  .footer-bottom {
    flex-direction: column;
    text-align: center;
    gap: 4px;
  }
}
</style>