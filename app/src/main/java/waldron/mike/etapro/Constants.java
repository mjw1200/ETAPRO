package waldron.mike.etapro;

public class Constants {
    /**
     * Uniquely identifies location services check, which makes sure location services is enabled. The value is almost completely arbitrary, except
     * that it has to be non-zero, positive, and it has to fit in 16 bits (< 32,768). See https://stackoverflow.com/a/48428074/4586740
     */
    final static int LOCATION_SERVICES_CHECK = 394;
}
