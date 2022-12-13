package org.example.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)
public enum PeriodicEnum {
    ONE_TIME(0),
    ONE_HOUR(1),
    TWO_HOURS(2),
    SIX_HOUR(6),
    TWELVE_HOURS(12);

    int period;

    PeriodicEnum(int period) {
        this.period = period;
    }
}