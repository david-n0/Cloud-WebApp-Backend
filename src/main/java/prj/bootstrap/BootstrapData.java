package prj.bootstrap;

import prj.model.User;
import prj.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import prj.model.*;
import prj.repositories.*;

import java.util.ArrayList;
import java.util.List;

@Component
public class BootstrapData implements CommandLineRunner {
    private final PermissionRepository permissionRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public BootstrapData(PermissionRepository permissionRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Loading Data...");

        Permissions[] PERMISSION_LIST = {Permissions.can_create_users, Permissions.can_read_users, Permissions.can_update_users, Permissions.can_delete_users,
                Permissions.can_search_nodes, Permissions.can_create_nodes, Permissions.can_destroy_nodes, Permissions.can_restart_nodes, Permissions.can_start_nodes, Permissions.can_stop_nodes};

        List<Permission> permissions = new ArrayList<>();
        for (int i = 0; i < PERMISSION_LIST.length; i++) {

            Permission permission = new Permission(PERMISSION_LIST[i]);
            permissions.add(permission);

            System.out.println(permissionRepository.save(permission));
        }

        User admin = new User();
        admin.setName("David");
        admin.setSurname("Nikolic");
        admin.setUsername("david@david.com");
        admin.setPassword(this.passwordEncoder.encode("dddd"));
        for(Permission p: permissions){
            admin.addPermission(p);
        }        this.userRepository.save(admin);

        System.out.println("Data loaded!");
    }
}
