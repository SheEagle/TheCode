<template>
  <footer class="site-footer">
    <div class="footer-container">
      <div class="footer-logo" style="width: 30%">
        <el-image src="/src/assets/the.png" style="width: 40%" alt="Logo"/>
      </div>
      <div class="footer-links">
        <h3>Break The Code</h3>
        <ul>
          <li><a href="https://womeninstem.org/" target="_blank">WOMAN IN STEM - WISTEM</a></li>
          <li><a href="/about">About us</a></li>
          <li><a href="/contact">Contact us</a></li>
        </ul>
      </div>
      <div class="footer-copyright">
        <p>Copyright © 2024 SheEagle. All rights reserved.</p>
      </div>
      <!--<div class="footer-additional-info">-->
      <!--  <p>Building up a world of bridges</p>-->
      <!--</div>-->
    </div>
  </footer>
</template>

<script>
export default {
  name: 'SiteFooter'
};
</script>

<style scoped>
.site-footer {
  background: linear-gradient(135deg, rgba(106, 17, 203, 0.42), rgba(37, 117, 252, 0.42));
  color: #fff;
  padding: 40px 20px;
  font-family: 'Roboto', sans-serif;
  text-align: center;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.3);
}

.footer-container {
  display: flex;
  justify-content: space-around;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.footer-logo img {
  height: 50px;
}

.footer-links h3,
.footer-additional-info p {
  margin-bottom: 20px;
  font-weight: 500;
  letter-spacing: 1px;
}

.footer-links ul {
  list-style: none;
  padding: 0;
  font-size: 15px;
}

.footer-links li {
  display: inline-block;
  margin: 0 10px;
}

.footer-links a,
.footer-additional-info p,
.footer-copyright p {
  color: #fff;
  text-decoration: none;
  transition: color 0.3s ease;
}

.footer-links a:hover,
.footer-additional-info p:hover,
.footer-copyright p:hover {
  color: #ddd;
}

.footer-copyright,
.footer-additional-info {
  flex: 1;
}

.footer-copyright p {
  margin: 10px 0;
  font-size: 0.9em;
}

@media (max-width: 768px) {
  .footer-container {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .footer-logo {
    margin-bottom: 20px;
  }

  .footer-links h3,
  .footer-additional-info p {
    margin-bottom: 10px;
  }

  .footer-links ul {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
  }

  .footer-links li {
    margin: 5px;
  }
}

/* 减小页脚内边距 */
.site-footer {
  padding: 20px 20px; /* 调整为更小的值 */
  /* 其他样式保持不变 */
}

/* 调整文本元素的字体大小和行高 */
.footer-copyright p {
  margin: 5px 0; /* 调整外边距 */
  font-size: 0.8em; /* 减小字体大小 */
  line-height: 1.2; /* 调整行高 */
}

/* 调整链接列表项的间距 */
.footer-links li {
  margin: 0 5px; /* 调整水平间距 */
}

/* 媒体查询中的调整 */
@media (max-width: 768px) {
  .footer-container {
    /* 其他样式保持不变 */
  }

  .footer-copyright p {
    margin: 5px 0; /* 调整为更小的值 */
  }

  /* 其他媒体查询中的调整 */
}
</style>