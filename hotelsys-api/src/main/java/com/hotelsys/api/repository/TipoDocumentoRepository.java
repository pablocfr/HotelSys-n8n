package com.hotelsys.api.repository;

import com.hotelsys.api.model.catalogos.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Integer> {
}
