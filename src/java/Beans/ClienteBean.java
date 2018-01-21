/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import com.burgosanchez.tcc.venta.ejb.Cliente;
import com.burgosanchez.tcc.venta.ejb.Persona;
import com.burgosanchez.tcc.venta.jpa.ClienteFacade;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

/**
 *
 * @author TID01
 */
@ManagedBean(name = "clienteBeanMB")
@ViewScoped
public class ClienteBean implements Serializable {

    @Inject
    private ClienteFacade clienteFacade;

    private Cliente cliente = new Cliente();
    private List<Cliente> clientes;
    @ManagedProperty("#{personaBeanMB.persona}")
    private Persona persona;
    private String re;
    public String tipop;
    public String nombre;

    public String getTipop() {
        return tipop;
    }

    public void setTipop(String tipop) {
        this.tipop = tipop;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ClienteBean() {
        //cliente = new Cliente();
    }

    public List<Cliente> getClientes() {
        /*if ((persona == null)) {
            re ="";
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("per", "%" + re + "%");
            clientes = clienteFacade.obtenerCliente(re);
        }*/
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getRe() {
        return re;
    }

    public void setRe(String re) {
        this.re = re;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public void obtenerCliente(Persona pe) {
        re = pe.getCodPersona().toLowerCase();
        //re = "%"+re+"%";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("per", "%" + re + "%");
        clientes = clienteFacade.obtenerCliente(re);
    }

    public void modificar() {
        clienteFacade.edit(cliente);
        clientes = clienteFacade.obtenerCliente(re);
    }

    public void eliminar() {
        clienteFacade.remove(cliente);
        clientes = clienteFacade.obtenerCliente(re);
    }

    public void obtenerClienteXnom() {
       /* re = nombre.toLowerCase();
        //re = "%"+re+"%";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nombre", "%" + re + "%");
        clientes = clienteFacade.obtenerClienteXnom(re);*/
    
     if (nombre != null) {
            try {
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("per", "%" + nombre + "%");
                clientes = clienteFacade.obtenerClienteXnom(nombre);
            } catch (Exception ex) {

            }
        } else {
            clientes = null;
        }
    }
}
