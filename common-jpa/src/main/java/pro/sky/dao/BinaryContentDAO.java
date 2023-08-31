package pro.sky.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.entity.BinaryContent;

public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {
}
