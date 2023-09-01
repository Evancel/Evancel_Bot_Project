package pro.sky.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.entity.AppTask;

import java.util.List;

public interface AppTaskDAO extends JpaRepository<AppTask, Long> {
    @Query(value = "select * from app_task where date_time=date_trunc('minute', current_timestamp)",nativeQuery = true)
    public List<AppTask> findAllTasks();
}
