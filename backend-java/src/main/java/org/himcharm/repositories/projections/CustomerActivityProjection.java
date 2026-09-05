package org.himcharm.repositories.projections;

public interface CustomerActivityProjection {

    long getTotalCustomers();

    long getActiveInThreeMonths();

    long getDormantThreeToSixMonths();

    long getDormantSixToTwelveMonths();

    long getDormantTwelvePlusMonths();
}
