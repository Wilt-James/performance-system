-- 更新admin用户密码为123456的正确BCrypt加密值
USE hospital_performance;

-- 更新admin用户密码 (明文密码: 123456)
UPDATE sys_user 
SET password = '$2a$10$7JB720yubVSOfvVWdBYoOe.PuiKloYAjFYcVtK9YB95aJR.Gt5Emi' 
WHERE username = 'admin';

-- 验证更新结果
SELECT id, username, password, real_name FROM sys_user WHERE username = 'admin';