package org.himcharm.repositories.projections;

public interface CustomerFrequencyProjection {

    long getOneTimeVisit();

    long getTwoTimesVisits();

    long getThreeTimesVisits();

    long getFourTimesVisits();

    long getFivePlusTimesVisits();

    long getTenPlusTimesVisits();
}
