package com.stock.entidades.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stock.entidades.Clientes;
import com.stock.repositorio.ClientesRepository;



@Service 
public class ClientesServiceImpl implements ClientesService{

	@Autowired
	private ClientesRepository repository;
	
	
	@Override
	public Clientes guardar(Clientes clienteDTO) {
	    return repository.save(clienteDTO);
	}
	
	@Override
	public List<Clientes> findAll() {
		return repository.findAll();
	}

	@Override
	@Transactional
	public Clientes findById(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		repository.deleteById(id);
		
	}

	@Override
    @Transactional(readOnly = true)
    public List<Clientes> findByNombreOrApellido(String term) {
        return repository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(term, term);
    }

	@Override
	public Page<Clientes> findAll(Pageable pageable, String palabraClave) {
		if (palabraClave != null && !palabraClave.isEmpty()) {
			return repository.findAll(palabraClave, pageable);
		}
		return repository.findAll(pageable);
	}

	@Override
	public long countAll() {
		return repository.count();
	}


}
