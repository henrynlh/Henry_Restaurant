package com.hk_restaurant.web;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.util.Map;

@RestControllerAdvice(assignableTypes=RestaurantApi.class)
public class ApiErrors {
    @ExceptionHandler({IllegalArgumentException.class,com.hk_restaurant.exception.ValidationException.class,
            com.hk_restaurant.exception.BusinessException.class,com.hk_restaurant.exception.AuthenticationException.class})
    public ResponseEntity<?> invalid(RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("message",e.getMessage())); }
    @ExceptionHandler(jakarta.persistence.PersistenceException.class)
    public ResponseEntity<?> database(Exception e) {
        org.slf4j.LoggerFactory.getLogger(getClass()).error("Database operation failed",e);
        return ResponseEntity.status(409).body(Map.of("message","Không thể lưu dữ liệu. Kiểm tra mã trùng hoặc dữ liệu đang được sử dụng."));
    }
}
