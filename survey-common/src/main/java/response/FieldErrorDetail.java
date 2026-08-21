package response;

public record FieldErrorDetail(
        String field,
        Object rejectValue,
        String reason) {
}
