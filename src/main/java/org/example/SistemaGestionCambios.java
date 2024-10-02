package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.*;

class Cliente {
    private String nombre;
    private int id;
    private int tiempoEstimadoInicial;
    private double costoInicial;

    public Cliente(String nombre, int id, int tiempoEstimadoInicial, double costoInicial) {
        this.nombre = nombre;
        this.id = id;
        this.tiempoEstimadoInicial = tiempoEstimadoInicial;
        this.costoInicial = costoInicial;
    }

    public String getNombre() {
        return nombre;
    }

    public int getId() {
        return id;
    }

    public int getTiempoEstimadoInicial() {
        return tiempoEstimadoInicial;
    }

    public double getCostoInicial() {
        return costoInicial;
    }

    @Override
    public String toString() {
        return "Cliente: " + nombre + " ID: " + id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Cliente cliente = (Cliente) obj;
        return id == cliente.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

class Cambio {
    String descripcion;
    String estado;
    Date fechaSolicitud;
    Cliente cliente;
    int impacto;
    int impactoEnDias;
    double costoAdicional;

    public Cambio(String descripcion, Cliente cliente, int impacto, int impactoEnDias, double costoAdicional) {
        this.descripcion = descripcion;
        this.cliente = cliente;
        this.estado = "Registrado";
        this.fechaSolicitud = new Date();
        this.impacto = impacto;
        this.impactoEnDias = impactoEnDias;
        this.costoAdicional = costoAdicional;
    }

    @Override
    public String toString() {
        String impactoTexto = impacto == 1 ? "Bajo" : impacto == 2 ? "Medio" : "Alto";
        return "Descripción del cambio: " + descripcion + ", Estado: " + estado + ", Impacto: " + impactoTexto +
                ", Días adicionales: " + impactoEnDias + ", Costo adicional: " + costoAdicional +
                ", Fecha Solicitud: " + fechaSolicitud + ", Cliente: " + cliente;
    }
}

public class SistemaGestionCambios {
    private ArrayList<Cambio> listaCambios = new ArrayList<>();
    private ArrayList<Cliente> listaClientes = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);
    private Stack<Cambio> pilaCambios = new Stack<>();
    private Queue<Cambio> colaCambios = new LinkedList<>();
    private int diasTotales = 0;
    private double costoTotal = 0;
    private int tiempoEstimadoInicial;
    private double costoInicial;

    public void iniciarSistema() {
        int opcion;

        do {
            System.out.println("\n--- Menú de Gestión de Cambios ---");
            System.out.println("1. Registrar un nuevo cliente");
            System.out.println("2. Registrar una nueva solicitud de cambio");
            System.out.println("3. Generar Reporte de Cambios");
            System.out.println("4. Deshacer último cambio");
            System.out.println("5. Visualizar cronograma actualizado");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1:
                        registrarCliente();
                        break;
                    case 2:
                        registrarSolicitudCambioRecursivo();
                        break;
                    case 3:
                        generarReporteExcel();
                        break;
                    case 4:
                        deshacerCambio();
                        break;
                    case 5:
                        visualizarCronograma();
                        break;
                    case 6:
                        System.out.println("Saliendo del sistema...");
                        break;
                    default:
                        System.out.println("Opción no válida, intente nuevamente.");
                }

            } catch (InputMismatchException e) {
                System.out.println("Opción inválida, ingrese una opción válida del menú");
                scanner.nextLine();
                opcion = 0;
            }

        } while (opcion != 6);
    }

    private void registrarCliente() {
        System.out.print("Ingrese el nombre del cliente: ");
        String nombre = scanner.nextLine();
        System.out.print("Ingrese el ID del cliente: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        for (Cliente c : listaClientes) {
            if (c.getNombre().equals(nombre)) {
                System.out.println("Dato existente. Registre otro nombre.");
                return;
            }
            if (c.getId() == id) {
                System.out.println("Dato existente. Registre otro código.");
                return;
            }
        }

        System.out.print("Ingrese el tiempo estimado inicial del proyecto (en días): ");
        tiempoEstimadoInicial = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Ingrese el costo inicial del proyecto (soles): ");
        costoInicial = scanner.nextDouble();
        scanner.nextLine();

        diasTotales = tiempoEstimadoInicial;
        costoTotal = costoInicial;

        Cliente cliente = new Cliente(nombre, id, tiempoEstimadoInicial, costoInicial);
        listaClientes.add(cliente);
        System.out.println("Cliente registrado exitosamente: " + cliente.toString());
    }

    private void registrarSolicitudCambioRecursivo() {
        if (listaClientes.isEmpty()) {
            System.out.println("No hay clientes registrados. Registre un cliente antes de solicitar cambios.");
            return;
        }

        System.out.println("Seleccione un cliente para el cambio:");
        for (int i = 0; i < listaClientes.size(); i++) {
            System.out.println((i + 1) + ". " + listaClientes.get(i).toString());
        }

        System.out.print("Ingrese el número del cliente: ");
        int indiceCliente = scanner.nextInt() - 1;
        scanner.nextLine();

        if (indiceCliente < 0 || indiceCliente >= listaClientes.size()) {
            System.out.println("Cliente no válido. Inténtelo de nuevo.");
            return;
        }

        Cliente clienteSeleccionado = listaClientes.get(indiceCliente);
        System.out.print("Ingrese la descripción del cambio: ");
        String descripcionCambio = scanner.nextLine();

        System.out.print("Ingrese el nivel de impacto (1 = Bajo, 2 = Medio, 3 = Alto): ");
        int impacto = scanner.nextInt();

        System.out.print("Ingrese el impacto en días: ");
        int impactoEnDias = scanner.nextInt();

        System.out.print("Ingrese el costo adicional: ");
        double costoAdicional = scanner.nextDouble();
        scanner.nextLine();

        Cambio nuevoCambio = new Cambio(descripcionCambio, clienteSeleccionado, impacto, impactoEnDias, costoAdicional);

        if (solicitarAprobacion(nuevoCambio.descripcion)) {
            listaCambios.add(nuevoCambio);
            colaCambios.offer(nuevoCambio);
            pilaCambios.push(nuevoCambio);
            actualizarCronograma(nuevoCambio);
            enviarNotificacion(String.valueOf(nuevoCambio));
            nuevoCambio.estado = "Aprobado";
            System.out.println("Cambio registrado exitosamente: " + nuevoCambio.toString());
        } else {
            nuevoCambio.estado = "Rechazado";
            System.out.println("Cambio rechazado: " + nuevoCambio.descripcion);
        }
    }

    public boolean solicitarAprobacion(String descripcionCambio) {
        System.out.print("¿Desea aprobar el cambio '" + descripcionCambio + "'? (sí/no): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();
        return respuesta.equals("sí");
    }

    private void enviarNotificacion(String mensaje) {
        System.out.println("NOTIFICACIÓN: " + mensaje);
    }

    private void generarReporteExcel() {
        if (listaClientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }

        System.out.println("Seleccione un cliente para generar el reporte:");
        for (int i = 0; i < listaClientes.size(); i++) {
            System.out.println((i + 1) + ". " + listaClientes.get(i).toString());
        }
        System.out.print("Ingrese el número del cliente: ");
        int indiceCliente = scanner.nextInt() - 1;
        scanner.nextLine();

        if (indiceCliente < 0 || indiceCliente >= listaClientes.size()) {
            System.out.println("Cliente no válido. Inténtelo de nuevo.");
            return;
        }

        Cliente clienteSeleccionado = listaClientes.get(indiceCliente);

        List<Cambio> cambiosDelCliente = new ArrayList<>();
        for (Cambio cambio : listaCambios) {
            if (cambio.cliente.equals(clienteSeleccionado)) {
                cambiosDelCliente.add(cambio);
            }
        }

        if (cambiosDelCliente.isEmpty()) {
            System.out.println("El cliente no tiene cambios registrados.");
            return;
        }

        int impactoTotalDias = 0;
        double costoAdicionalTotal = 0;

        for (Cambio cambio : cambiosDelCliente) {
            impactoTotalDias += cambio.impactoEnDias;
            costoAdicionalTotal += cambio.costoAdicional;
        }

        String nombreArchivo = clienteSeleccionado.getNombre().replace(" ", "_") + "_reporte.xlsx";

        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fileOut = new FileOutputStream(nombreArchivo)) {

            Sheet sheet = workbook.createSheet("Reporte Cambios");

            // Agregar encabezados
            Row encabezados = sheet.createRow(0);
            encabezados.createCell(0).setCellValue("Descripción");
            encabezados.createCell(1).setCellValue("Estado");
            encabezados.createCell(2).setCellValue("Impacto");
            encabezados.createCell(3).setCellValue("Días adicionales");
            encabezados.createCell(4).setCellValue("Costo adicional");
            encabezados.createCell(5).setCellValue("Fecha Solicitud");

            // Agregar cambios al reporte
            int rowNum = 1;
            for (Cambio cambio : cambiosDelCliente) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(cambio.descripcion);
                row.createCell(1).setCellValue(cambio.estado);
                row.createCell(2).setCellValue(cambio.impacto);
                row.createCell(3).setCellValue(cambio.impactoEnDias);
                row.createCell(4).setCellValue(cambio.costoAdicional);
                row.createCell(5).setCellValue(cambio.fechaSolicitud.toString());
            }

            //resumen
            Row resumen = sheet.createRow(rowNum);
            resumen.createCell(0).setCellValue("Impacto total en días:");
            resumen.createCell(1).setCellValue(impactoTotalDias);
            resumen.createCell(2).setCellValue("Costo total adicional:");
            resumen.createCell(3).setCellValue(costoAdicionalTotal);

            workbook.write(fileOut);
            System.out.println("Reporte generado: " + nombreArchivo);
        } catch (IOException e) {
            System.out.println("Error al generar el reporte: " + e.getMessage());
        }
    }

    private void deshacerCambio() {
        if (!pilaCambios.isEmpty()) {
            System.out.println("Cambios disponibles para deshacer:");
            for (int i = 0; i < pilaCambios.size(); i++) {
                Cambio cambio = pilaCambios.get(i);
                System.out.println((i + 1) + ". " + cambio.descripcion + " - Cliente: " + cambio.cliente.getNombre());
            }

            System.out.print("Seleccione el número del cambio que desea deshacer: ");
            int seleccion = scanner.nextInt() - 1;
            scanner.nextLine();

            if (seleccion < 0 || seleccion >= pilaCambios.size()) {
                System.out.println("Selección no válida. Inténtelo de nuevo.");
                return;
            }


            Cambio cambioSeleccionado = pilaCambios.get(seleccion);
            pilaCambios.remove(seleccion);
            diasTotales -= cambioSeleccionado.impactoEnDias;
            costoTotal -= cambioSeleccionado.costoAdicional;
            listaCambios.remove(cambioSeleccionado);
            System.out.println("Se ha deshecho el cambio: " + cambioSeleccionado.descripcion + " - Cliente: " + cambioSeleccionado.cliente.getNombre());
        } else {
            System.out.println("No hay cambios que deshacer.");
        }
    }

    private void visualizarCronograma() {
        if (listaClientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }

        System.out.println("Seleccione un cliente para visualizar el cronograma actualizado:");
        for (int i = 0; i < listaClientes.size(); i++) {
            System.out.println((i + 1) + ". " + listaClientes.get(i).toString());
        }
        System.out.print("Ingrese el número del cliente: ");
        int indiceCliente = scanner.nextInt() - 1;
        scanner.nextLine();

        if (indiceCliente < 0 || indiceCliente >= listaClientes.size()) {
            System.out.println("Cliente no válido. Inténtelo de nuevo.");
            return;
        }

        Cliente clienteSeleccionado = listaClientes.get(indiceCliente);

        List<Cambio> cambiosDelCliente = new ArrayList<>();
        for (Cambio cambio : listaCambios) {
            if (cambio.cliente.equals(clienteSeleccionado)) {
                cambiosDelCliente.add(cambio);
            }
        }

        if (!cambiosDelCliente.isEmpty()) {
            int impactoTotalDias = 0;
            double costoAdicionalTotal = 0;

            for (Cambio cambio : cambiosDelCliente) {
                impactoTotalDias += cambio.impactoEnDias;
                costoAdicionalTotal += cambio.costoAdicional;
            }

            System.out.println("Impacto acumulado en días: " + impactoTotalDias);
            System.out.println("Costo adicional total: " + costoAdicionalTotal);

            int nuevoTiempoEstimado = tiempoEstimadoInicial + impactoTotalDias;
            double nuevoCostoTotal = costoInicial + costoAdicionalTotal;
            System.out.println("Nuevo tiempo estimado del proyecto: " + nuevoTiempoEstimado + " días");
            System.out.println("Nuevo costo total del proyecto: " + nuevoCostoTotal + " soles");
        } else {
            System.out.println("El cliente no tiene cambios registrados.");
        }
    }

    private void actualizarCronograma(Cambio cambio) {
        diasTotales += cambio.impactoEnDias;
        costoTotal += cambio.costoAdicional;
    }

    public static void main(String[] args) {
        SistemaGestionCambios sistema = new SistemaGestionCambios();
        sistema.iniciarSistema();
    }
}
