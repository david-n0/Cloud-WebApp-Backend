package prj.repositories;

import prj.model.Permission;
import org.springframework.data.repository.CrudRepository;

public interface PermissionRepository extends CrudRepository<Permission, String> {
}
