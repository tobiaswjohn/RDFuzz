package no.uio.psy.rdfuzz.anomalies;

// class to collect anomalies to output them later in the report
public abstract class Anomaly implements Comparable {

    // adds information about current anomaly to the summary
    public AnomalySummary getSummary() {
        return new AnomalySummary();
    };

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int compareTo(Object o) {
        return  o.toString().compareTo(this.toString());
    }
}


