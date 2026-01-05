package com.example.commonlib.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // --- Lỗi liên quan đến hồ sơ ---
    PROFILE_NOT_FOUND(1001, "Không tìm thấy hồ sơ", HttpStatus.NOT_FOUND),
    INVALID_PROFILE_DATA(1002, "Dữ liệu hồ sơ không hợp lệ", HttpStatus.BAD_REQUEST),
    PROFILE_CREATION_FAILED(1003, "Tạo hồ sơ thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    PROFILE_UPDATE_FAILED(1004, "Cập nhật hồ sơ thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    PROFILE_DELETION_FAILED(1005, "Xóa hồ sơ thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_INVALID(1006, "Email không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_ADDRESS_DATA(1007, "Dữ liệu địa chỉ không hợp lệ", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(1008, "Lỗi máy chủ nội bộ", HttpStatus.INTERNAL_SERVER_ERROR),

    // --- Lỗi liên quan đến người dùng/tài khoản ---
    EMAIL_ALREADY_EXISTS(2001, "Email đã tồn tại", HttpStatus.CONFLICT),
    USERNAME_ALREADY_EXISTS(2002, "Tên đăng nhập đã tồn tại", HttpStatus.CONFLICT),
    USER_NOT_FOUND(2003, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    INVALID_USER_DATA(2004, "Dữ liệu người dùng không hợp lệ", HttpStatus.BAD_REQUEST),
    USER_CREATION_FAILED(2005, "Tạo người dùng thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    ADDRESS_NOT_FOUND(2006, "Không tìm thấy địa chỉ", HttpStatus.NOT_FOUND),
    ADDRESS_ALREADY_EXISTS(2007, "Địa chỉ đã tồn tại", HttpStatus.CONFLICT),
    INVALID_EMAIL(2008, "Email không hợp lệ", HttpStatus.BAD_REQUEST),
    ACCOUNT_DISABLED(2009, "Tài khoản bị vô hiệu hóa", HttpStatus.FORBIDDEN),
    PASSWORD_EXISTED(2010, "Mật khẩu đã tồn tại", HttpStatus.BAD_REQUEST),

    // --- Lỗi xác thực & phân quyền ---
    INVALID_REQUEST(3001, "Yêu cầu không hợp lệ", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(3002, "Truy cập trái phép", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(3003, "Truy cập bị từ chối", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS(3004, "Tên người dùng hoặc mật khẩu không đúng", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(3005, "Token xác thực hết hạn", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(3006, "Token xác thực không hợp lệ", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_INVALID(3007, "Token xác thực không hợp lệ", HttpStatus.UNAUTHORIZED),
    TOKEN_PARSING_ERROR(3008, "Lỗi phân tích token xác thực", HttpStatus.BAD_REQUEST),
    UNKNOWN_PROVIDER(3009, "Nhà cung cấp xác thực không xác định", HttpStatus.BAD_REQUEST),
    REDIRECT_URI_MISMATCH(3010, "Redirect URI không khớp", HttpStatus.BAD_REQUEST),
    OAUTH_PROVIDER_ERROR(3011, "Lỗi nhà cung cấp OAuth", HttpStatus.BAD_REQUEST),

    // --- Lỗi giỏ hàng ---
    CART_ITEM_NOT_FOUND(4001, "Không tìm thấy mục giỏ hàng", HttpStatus.NOT_FOUND),
    CART_NOT_FOUND(4002, "Không tìm thấy sản phẩm", HttpStatus.NOT_FOUND),
    INVALID_QUANTITY(4003, "Số lượng không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_CART_SELECTION(4004, "Lựa chọn giỏ hàng không hợp lệ", HttpStatus.BAD_REQUEST),
    CART_EMPTY(4005, "Giỏ hàng trống", HttpStatus.BAD_REQUEST),
    BAD_REQUEST(4006, "Yêu cầu không hợp lệ", HttpStatus.BAD_REQUEST),

    // --- Lỗi đơn hàng & vận chuyển ---
    OPERATION_FAILED(5001, "Thao tác thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_SHIPPING_ADDRESS(5002, "Địa chỉ giao hàng không hợp lệ", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(5003, "Không tìm thấy đơn hàng", HttpStatus.NOT_FOUND),
    ORDER_CANCELLATION_NOT_ALLOWED(5004, "Không được phép hủy đơn hàng", HttpStatus.BAD_REQUEST),
    ORDER_STATUS_UPDATE_NOT_ALLOWED(5005, "Không được phép cập nhật trạng thái đơn hàng", HttpStatus.BAD_REQUEST),
    ORDER_PAYMENT_FAILED(5006, "Thanh toán đơn hàng thất bại", HttpStatus.PAYMENT_REQUIRED),
    INTERNAL_ERROR(5007, "Lỗi nội bộ", HttpStatus.INTERNAL_SERVER_ERROR),

    // --- Lỗi sản phẩm ---
    PRODUCT_INFO_UNAVAILABLE(6001, "Thông tin sản phẩm không khả dụng", HttpStatus.SERVICE_UNAVAILABLE),
    PRODUCT_NOT_AVAILABLE(6002, "Sản phẩm không khả dụng", HttpStatus.NOT_FOUND),
    BRAND_NOT_FOUND(6003, "Không tìm thấy nhãn hàng", HttpStatus.NOT_FOUND),


    VNPAY_SIGNING_FAILED(7001, "Ký VNPAY thất bại", HttpStatus.INTERNAL_SERVER_ERROR),

    // --- Lỗi danh mục & sản phẩm ---
    INVALID_CATEGORY_NAME(8001, "Tên danh mục không hợp lệ", HttpStatus.BAD_REQUEST),
    CATEGORY_ALREADY_EXISTS(8002, "Danh mục đã tồn tại", HttpStatus.CONFLICT),
    CATEGORY_NOT_FOUND(8003, "Không tìm thấy danh mục", HttpStatus.NOT_FOUND),
    PRODUCT_ALREADY_EXISTS(8004, "Sản phẩm đã tồn tại", HttpStatus.CONFLICT),
    PRODUCT_NOT_FOUND(8005, "Không tìm thấy sản phẩm", HttpStatus.NOT_FOUND),
    INSUFFICIENT_STOCK(8006, "Hàng tồn kho không đủ", HttpStatus.BAD_REQUEST),

    // --- Lỗi khác ---
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
