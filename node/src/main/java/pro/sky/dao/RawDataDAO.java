package pro.sky.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.entity.RawData;

public interface RawDataDAO extends JpaRepository<RawData,Long> {
}
