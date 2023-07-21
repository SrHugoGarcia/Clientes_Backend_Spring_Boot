package org.hugo.springboot.backend.apirest.models.services;

import org.apache.juli.logging.Log;
import org.hugo.springboot.backend.apirest.models.entity.Cliente;

import java.util.List;

public interface IClienteService {
    Cliente createOne(Cliente cliente);
    Cliente updateOne(Cliente cliente);
    //void update(Cliente cliente);
    void deleteById(Long id);
    Cliente findOneById(Long id);
    List<Cliente> findAll();
}
