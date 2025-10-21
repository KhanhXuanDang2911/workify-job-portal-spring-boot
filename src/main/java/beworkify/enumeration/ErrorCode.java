package beworkify.enumeration;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_ROLE_ENUM(410, "error.invalid.role.enum"),
    INVALID_GENDER_ENUM(410, "error.invalid.gender.enum"),
    INVALID_STATUS_ENUM(410, "error.invalid.status.enum"),
    INVALID_STATUS_POST_ENUM(410, "error.invalid.status.post.enum"),
    INVALID_LEVEL_COMPANY_SIZE_ENUM(410, "error.invalid.level.company.size.enum"),
    INVALID_SALARY_TYPE_ENUM(410, "error.invalid.salary.type.enum"),
    INVALID_SALARY_UNIT_ENUM(410, "error.invalid.salary.unit.enum"),
    INVALID_EDUCATION_LEVEL_ENUM(410, "error.invalid.education.level.enum"),
    INVALID_JOB_LEVEL_ENUM(410, "error.invalid.job.level.enum"),
    INVALID_EXPERIENCE_LEVEL_ENUM(410, "error.invalid.experience.level.enum"),
    INVALID_JOB_TYPE_ENUM(410, "error.invalid.job.type.enum"),
    INVALID_AGE_TYPE_ENUM(410, "error.invalid.age.type.enum"),
    INVALID_JOB_STATUS_ENUM(410, "error.invalid.job.status.enum"),
    INVALID_JOB_GENDER_ENUM(410, "error.invalid.job.gender.enum"),
    INVALID_JOB_BENEFIT_ENUM(410, "error.invalid.job.benefit.enum"),
    VERIFY_EMAIL_FAILED(411, "user.verify.email.failed"),
    RESET_PASSWORD_FAILED(411, "user.reset.password.failed"),
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
