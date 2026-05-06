package com.pontevi.aquito.localizacao;

public class GeoUtils {

    public static float calcularBearing(
            double latOrigem, double lngOrigem,
            double latDestino, double lngDestino) {

        double lat1 = Math.toRadians(latOrigem);
        double lat2 = Math.toRadians(latDestino);
        double dLng = Math.toRadians(lngDestino - lngOrigem);

        double x = Math.sin(dLng) * Math.cos(lat2);
        double y = Math.cos(lat1) * Math.sin(lat2)
                - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLng);

        double bearing = Math.toDegrees(Math.atan2(x, y));
        return (float) ((bearing + 360) % 360);
    }
}