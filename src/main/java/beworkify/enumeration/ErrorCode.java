package beworkify.enumeration;

import lombok.Getter;

/**
 * Enumeration for application error codes and messages. Maps internal error codes to HTTP status
 * codes and message keys.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
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
  INVALID_APPLICATION_STATUS_ENUM(410, "error.invalid.application.status.enum"),
  VERIFY_EMAIL_FAILED(411, "user.verify.email.failed"),
  RESET_PASSWORD_FAILED(411, "user.reset.password.failed"),
  PASSWORD_MISMATCH(411, "user.password.mismatch"),
  ACCOUNT_NOT_ACTIVE(411, "user.not.active"),
  EMPLOYER_READ_APPLICATION(412, "employer.read.application"),
  ERROR_USER_AGENT_MOBILE_REQUIRED(412, "error.useragent.mobile.required"),
  UPLOAD_FILE_FAILED(501, "error.upload.failed"),
  BAD_REQUEST(400, "error.bad.request"),
  CONVERSATION_NOT_FOUND(404, "error.conversation.not.found"),
  MESSAGE_NOT_FOUND(404, "error.message.not.found"),
  NOT_CONVERSATION_PARTICIPANT(403, "error.not.conversation.participant"),
  APPLICANT_CANNOT_INITIATE(403, "error.applicant.cannot.initiate.conversation"),
  APPLICANT_MUST_WAIT_RECRUITER(403, "error.applicant.must.wait.recruiter");

  private final int code;
  private final String message;

  ErrorCode(int code, String message) {
    this.code = code;
    this.message = message;
  }
}
