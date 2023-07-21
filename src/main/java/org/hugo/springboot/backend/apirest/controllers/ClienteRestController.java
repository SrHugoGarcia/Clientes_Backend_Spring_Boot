package org.hugo.springboot.backend.apirest.controllers;

import jakarta.validation.Valid;
import org.apache.juli.logging.Log;
import org.hugo.springboot.backend.apirest.models.entity.Cliente;
import org.hugo.springboot.backend.apirest.models.services.IClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CrossOrigin(origins = {"http://localhost:4200"}, methods = {RequestMethod.GET,
        RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})
@RestController
@RequestMapping(value = "/api/v1/clientes")
public class ClienteRestController {
    @Autowired
    private IClienteService clienteService;

    @GetMapping(value = "")
    public ResponseEntity<?> findAll(){
        Map<String,Object> response = new HashMap<>();
        Map<String,List<Cliente>> listMap = new HashMap<>();
        listMap.put("clientes", clienteService.findAll());
        response.put("status", "successful");
        response.put("data",listMap);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        Cliente cliente = null;
        Map<String, Object> response = new HashMap<>();
        Map<String,Cliente> clienteMap = new HashMap<>();
        try{
            cliente = clienteService.findOneById(id);
        }catch (DataAccessException e){
            response.put("status","fail");
            response.put("message","Error en la consulta a la base de datos");
            response.put("error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(cliente == null){
            response.put("status","fail");
            response.put("message","Recurso no encontrado");
            response.put("error","El cliente ID: ".concat(id.toString()).concat(" no existe"));
            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }
        clienteMap.put("cliente",cliente);
        response.put("status","successful");
        response.put("message","Cliente encontrado con exito");
        response.put("data",clienteMap);
        return new ResponseEntity(response,HttpStatus.OK);
    }

    @PostMapping(value = "")
    public ResponseEntity<?> create(@Valid @RequestBody Cliente c, BindingResult result){
        Cliente cliente =  null;
        Map<String,Object> response = new HashMap<>();
        Map<String,Cliente> clienteMap = new HashMap<>();
        if(result.hasErrors()){
            /*List<String> errors = new ArrayList<>();
            for(FieldError err: result.getFieldErrors()){
                errors.add("El campo: ".concat(err.getField()).concat(" ").concat(err.getDefaultMessage()));
            }*/
            List<String> errors =  result.getFieldErrors().stream()
                    .map(err->"El campo: "
                            .concat(err.getField())
                            .concat(" ").
                            concat(err.getDefaultMessage()))
                    .collect(Collectors.toList());
                    response.put("status","fail");
            response.put("message", "Error en la creación del cliente");
            response.put("errors",errors);
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        try {
            cliente = clienteService.createOne(c);
        }catch (DataAccessException e){
            response.put("status","fail");
            response.put("message", "Error en la creación del cliente");
            response.put("error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        clienteMap.put("cliente", cliente);
        response.put("status", "successful");
        response.put("message", "La creacion del cliente fue exitosa");
        response.put("data",clienteMap);
        return new ResponseEntity(response,HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> update(@Valid @PathVariable Long id,@RequestBody Cliente c,BindingResult result){
        Cliente cliente = clienteService.findOneById(id);
        Map<String,Object> response = new HashMap<>();
        Map<String,Cliente> clienteMap = new HashMap<>();
        if(result.hasErrors()){
            /*List<String> errors = new ArrayList<>();
            for(FieldError err: result.getFieldErrors()){
                errors.add("El campo: ".concat(err.getField()).concat(" ").concat(err.getDefaultMessage()));
            }*/
            List<String> errors =  result.getFieldErrors().stream()
                    .map(err->"El campo: "
                            .concat(err.getField())
                            .concat(" ").
                            concat(err.getDefaultMessage()))
                    .collect(Collectors.toList());
            response.put("status","fail");
            response.put("message", "Error en la actualización del cliente");
            response.put("errors",errors);
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        if(cliente == null){
            response.put("status","fail");
            response.put("message", "Error en la actualización del cliente");
            response.put("error", "El cliente ID: "
                    .concat(id.toString())
                    .concat(" no existe en la base de datos, inserte otro valor"));
            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }
        cliente.setNombre(c.getNombre());
        cliente.setApellido(c.getApellido());
        cliente.setEmail(c.getEmail());
        Cliente clienteUpdate = null;
        try{
            clienteUpdate = clienteService.updateOne(cliente);
        }catch (DataAccessException e){
            response.put("status","fail");
            response.put("message", "Error en la actualización del cliente");
            response.put("error", "Error al realizar la actualización del registro en la base de datos"
                    .concat(e.getMessage())
                    .concat(": ")
                    .concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        clienteMap.put("cliente", clienteUpdate);
        response.put("status", "successful");
        response.put("message","Actualización del cliente con éxito");
        response.put("data",clienteMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Map<String,Object> response = new HashMap<>();
        try{
            clienteService.deleteById(id);
        }catch (DataAccessException e){
            response.put("status","fail");
            response.put("message", "Error al eliminar el cliente");
            response.put("error", "Error al realizar la eliminacion del registro en la base de datos"
                    .concat(e.getMessage())
                    .concat(": ")
                    .concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response,HttpStatus.NO_CONTENT);
    }


}
