package com.plango.api.dto.planningevent;

import java.time.LocalDateTime;

public interface PlanningEventDto {
    LocalDateTime getDateEnd();
    void setDateEnd(LocalDateTime localDateTime);
    LocalDateTime getDateStart();
    void setDateStart(LocalDateTime dateStart);
}
