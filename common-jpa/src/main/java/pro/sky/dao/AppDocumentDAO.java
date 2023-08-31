package pro.sky.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.entity.AppDocument;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
