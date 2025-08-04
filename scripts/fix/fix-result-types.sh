#!/bin/bash

echo "正在修复Result类型问题..."

# 修复PerformanceIndicatorController
sed -i 's/public Result<Void> createIndicator/public Result<String> createIndicator/g' src/main/java/com/hospital/performance/controller/PerformanceIndicatorController.java
sed -i 's/public Result<Void> updateIndicator/public Result<String> updateIndicator/g' src/main/java/com/hospital/performance/controller/PerformanceIndicatorController.java
sed -i 's/public Result<Void> deleteIndicator/public Result<String> deleteIndicator/g' src/main/java/com/hospital/performance/controller/PerformanceIndicatorController.java
sed -i 's/public Result<Void> batchDeleteIndicators/public Result<String> batchDeleteIndicators/g' src/main/java/com/hospital/performance/controller/PerformanceIndicatorController.java
sed -i 's/public Result<Void> updateIndicatorStatus/public Result<String> updateIndicatorStatus/g' src/main/java/com/hospital/performance/controller/PerformanceIndicatorController.java

# 修复HospitalOperationScoreController
sed -i 's/public Result<Void> recalculateScore/public Result<String> recalculateScore/g' src/main/java/com/hospital/performance/controller/HospitalOperationScoreController.java

# 修复PerformanceCalculationController
sed -i 's/public Result<Void> calculateDepartmentPerformance/public Result<String> calculateDepartmentPerformance/g' src/main/java/com/hospital/performance/controller/PerformanceCalculationController.java
sed -i 's/public Result<Void> calculateUserPerformance/public Result<String> calculateUserPerformance/g' src/main/java/com/hospital/performance/controller/PerformanceCalculationController.java
sed -i 's/public Result<Void> batchCalculatePerformance/public Result<String> batchCalculatePerformance/g' src/main/java/com/hospital/performance/controller/PerformanceCalculationController.java

# 修复DepartmentController
sed -i 's/public Result<Void> createDepartment/public Result<String> createDepartment/g' src/main/java/com/hospital/performance/controller/DepartmentController.java
sed -i 's/public Result<Void> updateDepartment/public Result<String> updateDepartment/g' src/main/java/com/hospital/performance/controller/DepartmentController.java
sed -i 's/public Result<Void> deleteDepartment/public Result<String> deleteDepartment/g' src/main/java/com/hospital/performance/controller/DepartmentController.java

# 修复UserController
sed -i 's/public Result<Void> createUser/public Result<String> createUser/g' src/main/java/com/hospital/performance/controller/UserController.java
sed -i 's/public Result<Void> updateUser/public Result<String> updateUser/g' src/main/java/com/hospital/performance/controller/UserController.java
sed -i 's/public Result<Void> deleteUser/public Result<String> deleteUser/g' src/main/java/com/hospital/performance/controller/UserController.java
sed -i 's/public Result<Void> resetPassword/public Result<String> resetPassword/g' src/main/java/com/hospital/performance/controller/UserController.java
sed -i 's/public Result<Void> updateUserStatus/public Result<String> updateUserStatus/g' src/main/java/com/hospital/performance/controller/UserController.java

echo "修复完成！"