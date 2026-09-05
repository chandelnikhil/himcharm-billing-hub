package org.himcharm.repositories.projections;

import java.time.LocalDate;

public interface CustomerSpecialDatesProjection {

    LocalDate getDateOfBirth();

    LocalDate getAnniversaryDate();
}
