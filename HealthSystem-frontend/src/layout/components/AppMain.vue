<template>
  <section class="app-main">
    <!-- 去掉 mode="out-in"，让新组件立即创建，不等待旧组件销毁，提升性能 -->
    <transition name="fade-transform">
      <!-- 完全禁用 keep-alive，每次切换页面都重新加载组件，确保释放资源，避免内存累积导致性能问题 -->
      <router-view :key="key" />
    </transition>
  </section>
</template>

<script>
export default {
  name: 'AppMain',
  computed: {
    key() {
      return this.$route.path
    }
  }
}
</script>

<style scoped>
.app-main {
  /* 84px = navbar(50px) + tagsView(34px) */
  min-height: calc(100vh - 84px);
  width: 100%;
  position: relative;
  /* 改为 auto，避免内容被裁剪，同时确保布局稳定 */
  overflow: auto;
}
.fixed-header+.app-main {
  /* 84px = navbar(50px) + tagsView(34px) */
  padding-top: 84px;
}
</style>

<style lang="scss">
// fix css style bug in open el-dialog
.el-popup-parent--hidden {
  .fixed-header {
    padding-right: 15px;
  }
}
</style>
