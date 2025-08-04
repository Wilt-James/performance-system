# 前端API调用修复总结

## 问题分析

您遇到的问题是前端页面调用以下API没有反应：
- `http://localhost:3000/performance/calculation`
- `http://localhost:3000/statistics/multi-dimension`
- `http://localhost:3000/statistics/operation-score`
- `http://localhost:3000/performance/indicator`

**根本原因**: 前端页面使用的是模拟数据，没有真正调用后端API接口。

## 修复内容

### 1. 创建API调用文件

#### `frontend/src/api/performance.js`
- 绩效计算相关API
- 绩效指标管理API
- 绩效方案和部门数据API

#### `frontend/src/api/statistics.js`
- 多维度统计分析API
- 医院运营评分API
- 通用统计API

### 2. 修复前端页面

#### 绩效计算页面 (`frontend/src/views/performance/Calculation.vue`)
- ✅ 添加真实API调用导入
- ✅ 修改 `loadInitialData()` 函数调用真实API
- ✅ 修改 `executeCalculation()` 函数调用真实API
- ✅ 修改 `loadCalculationHistory()` 函数调用真实API
- ✅ 修改 `viewResult()` 函数调用真实API

#### 多维度统计页面 (`frontend/src/views/statistics/MultiDimension.vue`)
- ✅ 添加真实API调用导入
- ✅ 修改 `executeQuery()` 函数调用真实API

#### 运营评分页面 (`frontend/src/views/statistics/OperationScore.vue`)
- ✅ 添加真实API调用导入
- ✅ 修改 `calculateScore()` 函数调用真实API
- ✅ 修改 `loadScoreHistory()` 函数调用真实API

#### 绩效指标页面 (`frontend/src/views/performance/Indicator.vue`)
- ✅ 添加真实API调用导入
- ✅ 修改 `loadData()` 函数调用真实API

### 3. 前端代理配置

前端已正确配置代理 (`frontend/vite.config.js`)：
```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
    rewrite: (path) => path.replace(/^\/api/, '/api')
  }
}
```

### 4. 后端CORS配置

后端已正确配置CORS (`src/main/java/com/hospital/performance/config/SecurityConfig.java`)：
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(Arrays.asList("*"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    // ...
}
```

## API路径映射

| 前端调用路径 | 实际后端API路径 |
|-------------|----------------|
| `/api/performance/calculation/execute` | `http://localhost:8080/api/performance/calculation/execute` |
| `/api/statistics/multi-dimension/data` | `http://localhost:8080/api/statistics/multi-dimension/data` |
| `/api/statistics/operation-score/data` | `http://localhost:8080/api/statistics/operation-score/data` |
| `/api/performance/indicator/list` | `http://localhost:8080/api/performance/indicator/list` |

## 启动服务

### 启动后端服务
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 启动前端服务
```bash
cd frontend
npm install  # 首次运行需要安装依赖
npm run dev
```

## 访问地址

- **前端地址**: http://localhost:3000
- **后端API**: http://localhost:8080/api

## 测试验证

### 使用测试脚本
```bash
# Linux/Mac
./test-frontend-backend.sh

# Windows
test-frontend-backend.bat
```

### 手动测试步骤

1. **确保服务运行**
   - 后端服务运行在 8080 端口
   - 前端服务运行在 3000 端口

2. **浏览器测试**
   - 访问 http://localhost:3000
   - 打开浏览器开发者工具
   - 查看 Network 标签页的API请求
   - 查看 Console 标签页的错误信息

3. **API测试**
   ```bash
   # 测试登录
   curl -X POST http://localhost:8080/api/auth/login \
        -H "Content-Type: application/json" \
        -d '{"username":"admin","password":"123456"}'
   
   # 测试绩效计算历史
   curl -X GET http://localhost:8080/api/performance/calculation/history
   ```

## 错误处理

所有修复的页面都包含错误处理机制：
- API调用失败时会回退到模拟数据
- 显示用户友好的错误消息
- 在控制台输出详细错误信息便于调试

## 注意事项

1. **首次登录**: 确保使用正确的用户名密码 (admin/123456)
2. **数据库连接**: 确保MySQL数据库正常运行
3. **端口冲突**: 确保8080和3000端口没有被其他程序占用
4. **依赖安装**: 前端首次运行需要执行 `npm install`

## 下一步

1. 启动前后端服务
2. 使用测试脚本验证连接
3. 在浏览器中测试各个页面功能
4. 如有问题，查看浏览器开发者工具的错误信息

修复完成后，前端页面应该能够正常调用后端API，不再使用模拟数据。