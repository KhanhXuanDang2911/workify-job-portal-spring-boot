package beworkify.enumeration;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_ROLE_ENUM(410, "error.invalid.role.enum"),
    INVALID_GENDER_ENUM(410, "error.invalid.gender.enum"),
    INVALID_STATUS_ENUM(410, "error.invalid.status.enum"),
    INVALID_STATUS_POST_ENUM(410, "error.invalid.status.post.enum"),
    INVALID_LEVEL_COMPANY_SIZE_ENUM(410, "error.invalid.level.company.size.enum"),
    VERIFY_EMAIL_FAILED(411, "user.verify.email.failed"),
    PASSWORD_MISMATCH(411, "user.password.mismatch"),
    ACCOUNT_NOT_ACTIVE(411, "user.not.active"),
    UPLOAD_FILE_FAILED(501, "error.upload.failed");
    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
