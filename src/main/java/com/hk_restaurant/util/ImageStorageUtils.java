package com.hk_restaurant.util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

/**
 * Tiện ích lưu trữ ảnh món ăn trong thư mục nội bộ của ứng dụng.
 *
 * <p>Ảnh được sao chép vào thư mục images/monan nằm cạnh thư mục chạy chương trình.
 * Database chỉ lưu đường dẫn tương đối, ví dụ: images/monan/lau_thai_ab12cd34.jpg</p>
 */
public final class ImageStorageUtils {

    public static final String DISH_IMAGE_DIR = "images/monan";

    private ImageStorageUtils() {
    }

    /**
     * Mở hộp thoại để người dùng chọn một file ảnh.
     *
     * @param parent component cha
     * @return file được chọn hoặc null nếu người dùng hủy
     */
    public static File chooseImageFile(Component parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn ảnh món ăn");
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Ảnh món ăn (*.png, *.jpg, *.jpeg, *.webp)",
                "png", "jpg", "jpeg", "webp"
        ));

        int result = chooser.showOpenDialog(parent);
        return result == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
    }

    /**
     * Sao chép file ảnh đã chọn vào thư mục images/monan và trả về đường dẫn tương đối để lưu DB.
     *
     * @param sourceFile ảnh gốc người dùng chọn
     * @return đường dẫn tương đối để lưu vào trường hinhAnh
     * @throws IOException nếu không thể sao chép file
     */
    public static String copyDishImageToStorage(File sourceFile) throws IOException {
        if (sourceFile == null || !sourceFile.exists()) {
            throw new IOException("Không tìm thấy file ảnh đã chọn.");
        }

        Path targetDir = Paths.get(System.getProperty("user.dir")).resolve(DISH_IMAGE_DIR);
        Files.createDirectories(targetDir);

        String originalName = sourceFile.getName();
        String extension = getExtension(originalName);
        String baseName = removeExtension(originalName);
        String safeBaseName = sanitizeFileName(baseName);

        String targetName = safeBaseName + "_" + UUID.randomUUID().toString().substring(0, 8)
                + (extension.isEmpty() ? "" : "." + extension.toLowerCase(Locale.ROOT));

        Path targetPath = targetDir.resolve(targetName);
        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return DISH_IMAGE_DIR.replace("\\", "/") + "/" + targetName;
    }

    /**
     * Quy đổi đường dẫn đã lưu trong DB sang đường dẫn tuyệt đối hiện tại để hiển thị ảnh.
     *
     * @param storedPath đường dẫn lưu trong DB
     * @return đường dẫn tuyệt đối, hoặc null nếu không hợp lệ
     */
    public static String resolveStoredPath(String storedPath) {
        if (storedPath == null || storedPath.trim().isEmpty()) {
            return null;
        }

        File directFile = new File(storedPath);
        if (directFile.exists()) {
            return directFile.getAbsolutePath();
        }

        Path resolved = Paths.get(System.getProperty("user.dir")).resolve(storedPath).normalize();
        return resolved.toString();
    }

    /**
     * Xóa file ảnh đã lưu trong thư mục nội bộ nếu tồn tại.
     *
     * @param storedPath đường dẫn tương đối đã lưu trong DB
     */
    public static void deleteStoredDishImageQuietly(String storedPath) {
        try {
            if (storedPath == null || storedPath.trim().isEmpty()) {
                return;
            }

            String normalized = storedPath.replace("\\", "/");
            if (!normalized.startsWith(DISH_IMAGE_DIR + "/")) {
                return;
            }

            Path absolute = Paths.get(System.getProperty("user.dir")).resolve(storedPath).normalize();
            Files.deleteIfExists(absolute);
        } catch (Exception ignored) {
        }
    }

    private static String getExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        return index >= 0 ? fileName.substring(index + 1) : "";
    }

    private static String removeExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        return index >= 0 ? fileName.substring(0, index) : fileName;
    }

    private static String sanitizeFileName(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        normalized = normalized.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        normalized = normalized.replaceAll("_+", "_");
        normalized = normalized.replaceAll("^_+|_+$", "");
        return normalized.isEmpty() ? "dish" : normalized.toLowerCase(Locale.ROOT);
    }
}
