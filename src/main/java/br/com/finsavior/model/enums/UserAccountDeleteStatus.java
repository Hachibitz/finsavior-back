package br.com.finsavior.model.enums;

import lombok.Getter;

@Getter
public enum UserAccountDeleteStatus {
    IN_PROCESS(1, "in_process"),
    FAILED(2, "failed"),
    FINISHED(3, "finished");

    private final Integer id;
    private final String description;

    UserAccountDeleteStatus(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    public static UserAccountDeleteStatus fromId(Integer id) {
        for (UserAccountDeleteStatus status : values()) {
            if (status.getId().equals(id)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid UserAccountDeleteStatus id: " + id);
    }
}
