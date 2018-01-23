/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.burgosanchez.tcc.venta.web.bean;

import com.burgosanchez.tcc.venta.ejb.EventoCab;
import com.burgosanchez.tcc.venta.ejb.Usuario;
import com.burgosanchez.tcc.venta.jpa.EventoCabFacade;
import com.burgosanchez.tcc.venta.web.common.Messages;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 *
 * @author TID01
 */
@ManagedBean(name = "eventoCabBeanMB")
@ViewScoped
public class EventoCabBean implements Serializable {

    @Inject
    private EventoCabFacade eventoFacade;

    private EventoCab evento;
    List<EventoCab> eventos;
    private String nombre;
    private String proveedor;
    
    @Resource(name = "tccResource")
    private DataSource dcorsOraResource;

    public EventoCabBean() {
        evento = new EventoCab();
    }

    public EventoCab getEvento() {
        return evento;
    }

    public void setEvento(EventoCab evento) {
        this.evento = evento;
    }

    public List<EventoCab> getEventos() {
        HttpSession session = SessionBean.getSession();
        Usuario user = (Usuario) session.getAttribute("username");
        String u = user.getNomUser();
        String tipo = (String) session.getAttribute("tipoUsuario");
        if (eventos == null) {
            if (user.getTipoUsuario().equals("1")) {
                eventos = eventoFacade.findAll();
            } else {
                eventos = eventoFacade.obtenerEventosUsuario(u);
            }
        }
        return eventos;
    }

    public void setEventos(List<EventoCab> eventos) {
        this.eventos = eventos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public List<EventoCab> completeEvent(String query) {
        List<EventoCab> allIdent = eventoFacade.findAll();
        List<EventoCab> filteredIdent = new ArrayList<>();

        for (int i = 0; i < allIdent.size(); i++) {
            EventoCab ide = allIdent.get(i);
            if (ide.getNombre().toLowerCase().startsWith(query)) {
                filteredIdent.add(ide);
            }
        }

        return filteredIdent;
    }

    public void insertar() {
        evento.setCodEvento(String.valueOf(eventoFacade.obtenerSecuenciaVal()));
        eventoFacade.create(evento);
        Messages.growlMessageInfo("Se creó exitosamente el evento", null);
        //evento = new EventoCab();
    }

    public void modificar() {
        eventoFacade.edit(evento);
        evento = new EventoCab();
        Messages.growlMessageInfo("Se modificó el evento", null);
    }

    public void eliminar() {
        eventoFacade.remove(evento);
        evento = new EventoCab();
        Messages.growlMessageInfo("Se eliminó exitosamente el evento", null);
    }

    public void obtenerParametroEvento() {
        if (nombre != null || proveedor != null) {
            if (nombre == null) {
                nombre = "";
            }
            if (proveedor == null) {
                proveedor = "";
            }
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("prov", "%" + proveedor.toLowerCase() + "%");
            parameters.put("nom", "%" + nombre.toLowerCase() + "%");

            eventos = eventoFacade.obtenerEvento(parameters);
        } else {
            eventos = null;
        }
    }

    /*  public String InsertarTipoEvento(String nameEvento) throws SQLException
    {
        conexionOracle op = new conexionOracle();
        mensaje= op.insertarTipoEvento(nameEvento);
        return mensaje;
    }
     */

    public void generarReporteEntradas(ActionEvent actionEvent) throws JRException, SQLException {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            Map<String, Object> parametros = new HashMap<>();
            HttpSession session = SessionBean.getSession();
            Usuario user = (Usuario) session.getAttribute("username");
            String u = user.getNomUser();
            List<EventoCab> eve = eventoFacade.obtenerEventosUsuario(u);
            parametros.put("p_cod_evento", eve.get(0).getCodEvento());
            
            File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes" + File.separator + "reporteDeEntradas.jasper"));
            System.out.println(jasper.getPath());
            String pr = jasper.getPath();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parametros, dcorsOraResource.getConnection());

//            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//            ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
//            ec.setResponseHeader("Content-Disposition", "attachment; filename=FacturacionMensual.pdf");
//            OutputStream output = ec.getResponseOutputStream();
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

            /*if (Reporformato.equals("1")) {
                response.addHeader("Content-disposition", "attachment; filename=FacturacionMensual_" + mes + anho + ".xls");
                JRExporter exporter = new JRXlsExporter();
                ServletOutputStream servletOutputStream = response.getOutputStream();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, servletOutputStream);
                exporter.exportReport();*/
            //} else {
            response.addHeader("Content-disposition", "attachment; filename=entradasVendidas.pdf");
            try (ServletOutputStream stream = response.getOutputStream()) {
                JasperExportManager.exportReportToPdfStream(jasperPrint, stream);

                stream.flush();
            }
            //}
            FacesContext.getCurrentInstance().responseComplete();
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

}
