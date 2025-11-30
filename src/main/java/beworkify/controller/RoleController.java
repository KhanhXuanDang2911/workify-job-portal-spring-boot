package beworkify.controller;

import beworkify.dto.request.RoleRequest;
import beworkify.dto.response.ResponseData;
import beworkify.dto.response.RoleResponse;
import beworkify.service.RoleService;
import beworkify.util.ResponseBuilder;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(value = "/api/v1/roles")
public class RoleController {

  private final RoleService roleService;
  private final MessageSource messageSource;

  @GetMapping
  public ResponseEntity<ResponseData<List<RoleResponse>>> getAll() {
    log.info("Request: Get all roles");
    List<RoleResponse> response = roleService.getAllRoles();
    log.info("Response: {} roles found", response.size());
    String message =
        messageSource.getMessage("role.get.list.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @GetMapping("/{name}")
  public ResponseEntity<ResponseData<RoleResponse>> getByRoleName(
      @PathVariable("name") String name) {
    log.info("Request: Get role by name = {}", name);
    RoleResponse response = roleService.getRoleByRoleName(name);
    log.info("Response: Found role = {}", response);
    String message =
        messageSource.getMessage("role.get.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @PostMapping
  public ResponseEntity<ResponseData<RoleResponse>> create(
      @RequestBody @Validated RoleRequest request) {
    log.info("Request: Create role with data = {}", request);
    RoleResponse response = roleService.createRole(request);
    log.info("Response: Role created = {}", response);
    String message =
        messageSource.getMessage("role.create.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.CREATED, message, response);
  }

  @PutMapping
  public ResponseEntity<ResponseData<RoleResponse>> update(
      @RequestParam @Min(value = 1, message = "{validation.id.min}") Long id,
      @RequestBody @Validated RoleRequest request) {
    log.info("Request: Update role with id = {}, data = {}", id, request);
    RoleResponse response = roleService.updateRole(request, id);
    log.info("Response: Role updated = {}", response);
    String message =
        messageSource.getMessage("role.update.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ResponseData<Void>> delete(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min.role}") Long id) {
    log.info("Request: Delete role with id = {}", id);
    roleService.deleteRole(id);
    log.info("Response: Role deleted with id = {}", id);
    String message =
        messageSource.getMessage("role.delete.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.noData(HttpStatus.OK, message);
  }
}
