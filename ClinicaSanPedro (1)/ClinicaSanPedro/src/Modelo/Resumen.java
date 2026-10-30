package Modelo;

/** Totales de un reporte: citas por estado y ganancia (suma de citas atendidas). */
public class Resumen {

    private final int atendidas;
    private final int canceladas;
    private final int pendientes;
    private final double ganancia;

    public Resumen(int atendidas, int canceladas, int pendientes, double ganancia) {
        this.atendidas = atendidas;
        this.canceladas = canceladas;
        this.pendientes = pendientes;
        this.ganancia = ganancia;
    }

    public int getAtendidas() { return atendidas; }
    public int getCanceladas() { return canceladas; }
    public int getPendientes() { return pendientes; }
    public double getGanancia() { return ganancia; }
    public int getTotal() { return atendidas + canceladas + pendientes; }
}
