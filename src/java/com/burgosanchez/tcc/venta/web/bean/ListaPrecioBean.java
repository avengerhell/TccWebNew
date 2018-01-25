/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.burgosanchez.tcc.venta.web.bean;

import com.burgosanchez.tcc.venta.ejb.ListaPrecio;
import com.burgosanchez.tcc.venta.ejb.ListaPrecioPK;
import com.burgosanchez.tcc.venta.ejb.Sector;
import com.burgosanchez.tcc.venta.jpa.ListaPrecioFacade;
import com.burgosanchez.tcc.venta.jpa.SectorFacade;
import com.burgosanchez.tcc.venta.web.common.Messages;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

/**
 *
 * @author TID01
 */
@ManagedBean(name = "listaPrecioBeanMB")
@ViewScoped
public class ListaPrecioBean implements Serializable {

    @Inject
    private ListaPrecioFacade listaFacade;
    @Inject
    private SectorFacade sectorFacade;

    private ListaPrecio lista;
    List<ListaPrecio> listas;
    List<Sector> sectores;
    private ListaPrecioPK listaPK;
    String evento;

    public ListaPrecioBean() {
        lista = new ListaPrecio();
        listaPK = new ListaPrecioPK();
    }

    public ListaPrecio getLista() {
        return lista;
    }

    public void setLista(ListaPrecio lista) {
        this.lista = lista;
    }

    public List<ListaPrecio> getListas() {
        return listas;
    }

    public void setListas(List<ListaPrecio> listas) {
        this.listas = listas;
    }

    public List<Sector> getSectores() {
        if (lista.getEventoCab() == null) {//|| lista.getEventoCab().getCodEvento().isEmpty()) {
            sectores = sectorFacade.findAll();
        } else {
            sectores = (List<Sector>) sectorFacade.obtenerSectorEven(lista.getEventoCab().getCodEvento());
        }
        return sectores;
    }

    public void setSectores(List<Sector> sectores) {
        this.sectores = sectores;
    }

    public void obtenerListaPrecioEvento(String cod) {

    }

    public void insertar() {
        listaPK.setCodLista(String.valueOf(listaFacade.obtenerSecuenciaVal()));
        if (evento != null) {
            listaPK.setCodEvento(evento);
        } else {
            listaPK.setCodEvento(lista.getEventoCab().getCodEvento());
        }
        listaPK.setCodSector(lista.getSector().getSectorPK().getCodSector());
        lista.setListaPrecioPK(listaPK);
        listaFacade.create(lista);
        evento = lista.getEventoCab().getCodEvento();
        lista = new ListaPrecio();

        Messages.growlMessageInfo("Ingresado Correctamente", null);

    }

    public void modificar() {
        listaFacade.edit(lista);
        lista = new ListaPrecio();
        Messages.growlMessageInfo("Modificado Correctamente", null);
    }

    public void eliminar() {
        listaFacade.remove(lista);
        lista = new ListaPrecio();
        Messages.growlMessageInfo("Eliminado Correctamente", null);
    }

    public void onEventChange() {
        if (lista.getEventoCab() != null && !lista.getEventoCab().equals("")) {
            sectores = (List<Sector>) sectorFacade.obtenerSectorEven(lista.getEventoCab().getCodEvento());
        }
    }
}
