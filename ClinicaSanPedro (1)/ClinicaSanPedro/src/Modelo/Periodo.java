package Modelo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/** Rango de fechas para los reportes: diario, semanal, mensual o anual. */
public class Periodo {

    public static final String DIARIO = "Diario";
    public static final String SEMANAL = "Semanal";
    public static final String MENSUAL = "Mensual";
    public static final String ANUAL = "Anual";

    private final String tipo;
    private final LocalDate desde;
    private final LocalDate hasta;

    public Periodo(String tipo, LocalDate referencia) {
        this.tipo = tipo;
        switch (tipo) {
            case SEMANAL:
                desde = referencia.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                hasta = referencia.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                break;
            case MENSUAL:
                desde = referencia.with(TemporalAdjusters.firstDayOfMonth());
                hasta = referencia.with(TemporalAdjusters.lastDayOfMonth());
                break;
            case ANUAL:
                desde = referencia.with(TemporalAdjusters.firstDayOfYear());
                hasta = referencia.with(TemporalAdjusters.lastDayOfYear());
                break;
            default:
                desde = referencia;
                hasta = referencia;
        }
    }

    public String getTipo() { return tipo; }
    public LocalDate getDesde() { return desde; }
    public LocalDate getHasta() { return hasta; }

    @Override
    public String toString() {
        if (desde.equals(hasta)) {
            return desde.format(Cita.FORMATO_FECHA);
        }
        return desde.format(Cita.FORMATO_FECHA) + " al " + hasta.format(Cita.FORMATO_FECHA);
    }
}
