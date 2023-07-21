package org.hugo.springboot.backend.apirest.dao;

import org.hugo.springboot.backend.apirest.models.entity.Cliente;
import org.springframework.data.repository.CrudRepository;

public interface IClienteDao extends CrudRepository<Cliente,Long>{

}
