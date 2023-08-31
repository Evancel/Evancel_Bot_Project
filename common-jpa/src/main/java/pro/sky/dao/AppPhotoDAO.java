package pro.sky.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.entity.AppPhoto;

public interface AppPhotoDAO extends JpaRepository<AppPhoto,Long> {
}
