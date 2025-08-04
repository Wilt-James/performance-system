import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 数据库中的加密密码
        String dbPassword = "$2a$10$7JB720yubVSOfvVWbGRCu.VGaLIxZHjmQxzQbHjPT9db9dF00miD.";
        
        // 测试常见密码
        String[] testPasswords = {"123456", "admin", "password", "123", "admin123"};
        
        System.out.println("测试数据库中的密码: " + dbPassword);
        System.out.println();
        
        for (String testPassword : testPasswords) {
            boolean matches = encoder.matches(testPassword, dbPassword);
            System.out.println("密码 '" + testPassword + "' 匹配结果: " + matches);
        }
        
        System.out.println();
        System.out.println("生成新的123456密码:");
        String newPassword = encoder.encode("123456");
        System.out.println(newPassword);
        
        System.out.println();
        System.out.println("验证新密码:");
        System.out.println("123456 匹配新密码: " + encoder.matches("123456", newPassword));
    }
}