package com.hk_restaurant.util.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Lớp tiện ích bảo mật cho mật khẩu.
 *
 * <p>Phiên bản nâng cấp này hỗ trợ hai cơ chế:</p>
 * <ul>
 *   <li>PBKDF2WithHmacSHA256 cho mật khẩu mới tạo hoặc đổi mới.</li>
 *   <li>MD5 legacy để tương thích với dữ liệu mẫu cũ trong SQL.</li>
 * </ul>
 * Nhờ đó, project vẫn chạy được ngay với bộ dữ liệu hiện tại,
 * đồng thời bảo mật hơn cho các mật khẩu phát sinh về sau.</p>
 */
public final class PasswordUtils {

    private static final String PBKDF2_PREFIX = "PBKDF2";
    private static final int ITERATIONS = 65_536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private PasswordUtils() {
    }

    /**
     * Băm mật khẩu mới bằng PBKDF2.
     *
     * @param rawPassword mật khẩu gốc người dùng nhập
     * @return chuỗi hash đã kèm metadata và salt
     */
    public static String hash(String rawPassword) {
        if (rawPassword == null) {
            return null;
        }

        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        byte[] hashed = pbkdf2(rawPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

        return PBKDF2_PREFIX
                + "$" + ITERATIONS
                + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(hashed);
    }

    /**
     * So khớp mật khẩu người dùng nhập với mật khẩu đang lưu trong cơ sở dữ liệu.
     * Hàm này hỗ trợ cả dữ liệu hash mới (PBKDF2) và dữ liệu legacy dạng MD5.
     *
     * @param rawPassword mật khẩu gốc người dùng nhập
     * @param storedPassword mật khẩu đã lưu trong DB
     * @return true nếu khớp, ngược lại false
     */
    public static boolean matches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }

        String normalized = storedPassword.trim();

        if (isPbkdf2Hash(normalized)) {
            return matchesPbkdf2(rawPassword, normalized);
        }

        return md5(rawPassword).equalsIgnoreCase(normalized);
    }

    /**
     * Kiểm tra chuỗi hash hiện tại có thuộc định dạng MD5 cũ hay không.
     * Hàm này hữu ích khi muốn nâng cấp dần dữ liệu đăng nhập legacy.
     *
     * @param storedPassword mật khẩu đã lưu trong DB
     * @return true nếu là hash MD5 cũ
     */
    public static boolean isLegacyMd5Hash(String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        return storedPassword.trim().matches("(?i)^[a-f0-9]{32}$");
    }

    /**
     * Kiểm tra chuỗi hash hiện tại có thuộc định dạng PBKDF2 hay không.
     *
     * @param storedPassword mật khẩu đã lưu trong DB
     * @return true nếu là hash PBKDF2
     */
    public static boolean isPbkdf2Hash(String storedPassword) {
        return storedPassword != null && storedPassword.startsWith(PBKDF2_PREFIX + "$");
    }

    /**
     * So khớp mật khẩu với hash PBKDF2.
     *
     * @param rawPassword mật khẩu gốc
     * @param storedPassword chuỗi hash PBKDF2 đã lưu
     * @return true nếu khớp, ngược lại false
     */
    private static boolean matchesPbkdf2(String rawPassword, String storedPassword) {
        try {
            String[] parts = storedPassword.split("\\$");
            if (parts.length != 4) {
                return false;
            }

            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[3]);

            byte[] actualHash = pbkdf2(rawPassword.toCharArray(), salt, iterations, expectedHash.length * 8);
            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Sinh hash PBKDF2 từ mật khẩu đầu vào.
     *
     * @param password mật khẩu gốc
     * @param salt salt ngẫu nhiên
     * @param iterations số vòng lặp
     * @param keyLength độ dài khóa cần sinh
     * @return mảng byte hash
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return factory.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new RuntimeException("Không thể băm mật khẩu bằng PBKDF2", e);
        }
    }

    /**
     * Băm chuỗi theo MD5 để tương thích với dữ liệu mẫu cũ.
     *
     * @param input chuỗi đầu vào
     * @return chuỗi hash MD5 dạng hex
     */
    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Không hỗ trợ MD5", e);
        }
    }
}
