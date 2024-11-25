package no.uio.psy.rdfuzz.anomalies;

// a small class to summarize which anomalies have been found / how to clasify them
public class AnomalySummary {
    public boolean inEL = true;
    public int exceptions = 0;
    public int consistencyAnomalies = 0;
    public int inferenceAnomalies = 0;

    // adds another summary to this one
    public void add(AnomalySummary as) {
        if (!as.inEL)
            this.inEL = false;

        this.exceptions += as.exceptions;
        this.consistencyAnomalies += as.consistencyAnomalies;
        this.inferenceAnomalies += as.inferenceAnomalies;
    }

    public String toCSVString(String delimiter) {
        return inEL + delimiter +
                exceptions + delimiter +
                consistencyAnomalies + delimiter +
                inferenceAnomalies;
    }
}
