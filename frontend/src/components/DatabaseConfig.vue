<template>
  <el-card class="database-config">
    <template #header>
      <span>系统信息</span>
    </template>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="系统状态">
        <el-tag type="success">运行中</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="后端服务">
        <el-tag type="success">已连接</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="数据库">
        PostgreSQL
      </el-descriptions-item>
      <el-descriptions-item label="API 地址">
        当前部署配置
      </el-descriptions-item>
    </el-descriptions>
  </el-card>
</template>

<script setup lang="ts">
</script>

<style scoped>
.database-config {
  margin-bottom: 20px;
}
</style>
